package com.capacitorjs.plugins.share;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.*;
import android.net.Uri;
import android.os.Build;
import android.webkit.MimeTypeMap;
import androidx.activity.result.ActivityResult;
import androidx.core.content.FileProvider;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.ActivityCallback;
import com.getcapacitor.annotation.CapacitorPlugin;
import java.io.File;

@CapacitorPlugin(name = "Share")
public class SharePlugin extends Plugin {

    private BroadcastReceiver broadcastReceiver;
    private boolean stopped = false;
    private boolean isPresenting = false;
    private ComponentName chosenComponent;

    @Override
    public void load() {
        broadcastReceiver =
            new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    chosenComponent = intent.getParcelableExtra(Intent.EXTRA_CHOSEN_COMPONENT);
                }
            };
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(Intent.EXTRA_CHOSEN_COMPONENT));
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
            String dialogTitle = call.getString("dialogTitle", "Share");

            if (text == null && url == null) {
                call.reject("Must provide a URL or Message");
                return;
            }

            if (url != null && !isFileUrl(url) && !isHttpUrl(url)) {
                call.reject("Unsupported url");
                return;
            }

            Intent intent = new Intent(Intent.ACTION_SEND);

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
                String type = getMimeType(url);
                if (type == null) {
                    type = "*/*";
                }
                intent.setType(type);
                try {
                    Uri fileUrl = FileProvider.getUriForFile(
                        getActivity(),
                        getContext().getPackageName() + ".fileprovider",
                        new File(Uri.parse(url).getPath())
                    );
                    intent.putExtra(Intent.EXTRA_STREAM, fileUrl);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        intent.setDataAndType(fileUrl, type);
                    }
                } catch (Exception ex) {
                    call.reject(ex.getLocalizedMessage());
                    return;
                }

                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }

            if (title != null) {
                intent.putExtra(Intent.EXTRA_SUBJECT, title);
            }
            int flags = PendingIntent.FLAG_UPDATE_CURRENT;
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                flags = flags | PendingIntent.FLAG_MUTABLE;
            }

            // requestCode parameter is not used. Providing 0
            PendingIntent pi = PendingIntent.getBroadcast(getContext(), 0, new Intent(Intent.EXTRA_CHOSEN_COMPONENT), flags);
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

    @Override
    protected void handleOnDestroy() {
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
