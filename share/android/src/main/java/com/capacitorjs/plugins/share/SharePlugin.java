package com.capacitorjs.plugins.share;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.*;
import android.net.Uri;
import android.os.Build;
import android.webkit.MimeTypeMap;
import androidx.activity.result.ActivityResult;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.ActivityCallback;
import com.getcapacitor.annotation.CapacitorPlugin;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.json.JSONException;

@CapacitorPlugin(name = "Share")
public class SharePlugin extends Plugin {

    private static final String NONCE_EXTRA_KEY = "_share_nonce";

    private BroadcastReceiver broadcastReceiver;
    private boolean stopped = false;
    private boolean isPresenting = false;
    private ComponentName chosenComponent;
    private String expectedNonce;

    @Override
    public void load() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Validate nonce to prevent spoofing from other apps
                //  Reference: https://github.com/ionic-team/capacitor-plugins/pull/2592
                String receivedNonce = intent.getStringExtra(NONCE_EXTRA_KEY);
                if (receivedNonce == null || !receivedNonce.equals(expectedNonce)) {
                    // Reject broadcasts that don't have the correct nonce
                    return;
                }

                ComponentName component;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    component = intent.getParcelableExtra(Intent.EXTRA_CHOSEN_COMPONENT, ComponentName.class);
                } else {
                    component = getParcelableExtraLegacy(intent, Intent.EXTRA_CHOSEN_COMPONENT);
                }

                // Only clear nonce if we successfully got the component data
                if (component != null) {
                    chosenComponent = component;
                    expectedNonce = null;
                }
            }
        };
        ContextCompat.registerReceiver(
            getContext(),
            broadcastReceiver,
            new IntentFilter(Intent.EXTRA_CHOSEN_COMPONENT),
            ContextCompat.RECEIVER_EXPORTED
        );
    }

    @SuppressWarnings("deprecation")
    private ComponentName getParcelableExtraLegacy(Intent intent, String string) {
        return intent.getParcelableExtra(string);
    }

    @ActivityCallback
    private void activityResult(PluginCall call, ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_CANCELED && !stopped) {
            call.reject("Share canceled");
        } else {
            JSObject callResult = new JSObject();
            callResult.put("activityType", chosenComponent != null ? chosenComponent.getPackageName() : "");
            call.resolve(callResult);
        }
        isPresenting = false;
        expectedNonce = null;
    }

    @PluginMethod
    public void canShare(PluginCall call) {
        JSObject callResult = new JSObject();
        callResult.put("value", true);
        call.resolve(callResult);
    }

    @PluginMethod
    public void share(PluginCall call) {
        if (!isPresenting) {
            String title = call.getString("title", "");
            String text = call.getString("text");
            String url = call.getString("url");
            JSArray files = call.getArray("files");
            String dialogTitle = call.getString("dialogTitle", "Share");

            if (text == null && url == null && (files == null || files.length() == 0)) {
                call.reject("Must provide a URL or Message or files");
                return;
            }

            if (url != null && !isFileUrl(url) && !isHttpUrl(url)) {
                call.reject("Unsupported url");
                return;
            }

            Intent intent = new Intent(files != null && files.length() > 1 ? Intent.ACTION_SEND_MULTIPLE : Intent.ACTION_SEND);

            if (text != null) {
                // If they supplied both fields, concat them
                if (url != null && isHttpUrl(url)) text = text + " " + url;
                intent.putExtra(Intent.EXTRA_TEXT, text);
                intent.setTypeAndNormalize("text/plain");
            }

            if (url != null && isHttpUrl(url) && text == null) {
                intent.putExtra(Intent.EXTRA_TEXT, url);
                intent.setTypeAndNormalize("text/plain");
            } else if (url != null && isFileUrl(url)) {
                JSArray filesArray = new JSArray();
                filesArray.put(url);
                shareFiles(filesArray, intent, call);
            }

            if (title != null) {
                intent.putExtra(Intent.EXTRA_SUBJECT, title);
            }

            if (files != null && files.length() != 0) {
                shareFiles(files, intent, call);
            }

            // Generate a random nonce to prevent spoofing via exported receiver
            expectedNonce = UUID.randomUUID().toString();
            Intent callbackIntent = new Intent(Intent.EXTRA_CHOSEN_COMPONENT);
            callbackIntent.putExtra(NONCE_EXTRA_KEY, expectedNonce);

            int flags = PendingIntent.FLAG_UPDATE_CURRENT;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                flags = flags | PendingIntent.FLAG_MUTABLE;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                flags = flags | PendingIntent.FLAG_ALLOW_UNSAFE_IMPLICIT_INTENT;
            }

            // requestCode parameter is not used. Providing 0
            PendingIntent pi = PendingIntent.getBroadcast(getContext(), 0, callbackIntent, flags);
            Intent chooser = Intent.createChooser(intent, dialogTitle, pi.getIntentSender());
            chosenComponent = null;
            chooser.addCategory(Intent.CATEGORY_DEFAULT);
            stopped = false;
            isPresenting = true;
            startActivityForResult(call, chooser, "activityResult");
        } else {
            call.reject("Can't share while sharing is in progress");
        }
    }

    private void shareFiles(JSArray files, Intent intent, PluginCall call) {
        List<Object> filesList;
        ArrayList<Uri> fileUris = new ArrayList<>();
        try {
            filesList = files.toList();
            for (int i = 0; i < filesList.size(); i++) {
                String file = (String) filesList.get(i);
                if (isFileUrl(file)) {
                    String type = getMimeType(file);
                    if (type == null || filesList.size() > 1) {
                        type = "*/*";
                    }
                    intent.setType(type);

                    Uri fileUrl = FileProvider.getUriForFile(
                        getActivity(),
                        getContext().getPackageName() + ".fileprovider",
                        new File(Uri.parse(file).getPath())
                    );
                    fileUris.add(fileUrl);
                } else {
                    call.reject("only file urls are supported");
                    return;
                }
            }
            if (fileUris.size() > 1) {
                intent.putExtra(Intent.EXTRA_STREAM, fileUris);
            } else if (fileUris.size() == 1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    intent.setClipData(ClipData.newRawUri("", fileUris.get(0)));
                }
                intent.putExtra(Intent.EXTRA_STREAM, fileUris.get(0));
            }
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } catch (Exception ex) {
            call.reject(ex.getLocalizedMessage());
            return;
        }
    }

    @Override
    protected void handleOnDestroy() {
        expectedNonce = null;
        if (broadcastReceiver != null) {
            getActivity().unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    protected void handleOnStop() {
        super.handleOnStop();
        stopped = true;
    }

    private String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    private boolean isFileUrl(String url) {
        return url.startsWith("file:");
    }

    private boolean isHttpUrl(String url) {
        return url.startsWith("http");
    }
}
