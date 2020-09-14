package com.capacitorjs.plugins.share;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.*;
import android.net.Uri;
import android.os.Build;
import android.webkit.MimeTypeMap;
import androidx.core.content.FileProvider;
import com.getcapacitor.*;
import java.io.File;

@NativePlugin(name = "Share", requestCodes = { SharePlugin.REQUEST_SHARE })
public class SharePlugin extends Plugin {
    static final int REQUEST_SHARE = 9023;

    private BroadcastReceiver broadcastReceiver;
    private boolean stopped = false;
    private ComponentName chosenComponent;

    @Override
    public void load() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            broadcastReceiver =
                new BroadcastReceiver() {

                    @Override
                    public void onReceive(Context context, Intent intent) {
                        chosenComponent = intent.getParcelableExtra(Intent.EXTRA_CHOSEN_COMPONENT);
                    }
                };
            getActivity().registerReceiver(broadcastReceiver, new IntentFilter(Intent.EXTRA_CHOSEN_COMPONENT));
        }
    }

    @PluginMethod
    public void share(PluginCall call) {
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
            intent.setType(type);
            Uri fileUrl = FileProvider.getUriForFile(
                getActivity(),
                getContext().getPackageName() + ".fileprovider",
                new File(Uri.parse(url).getPath())
            );
            intent.putExtra(Intent.EXTRA_STREAM, fileUrl);
        }

        if (title != null) {
            intent.putExtra(Intent.EXTRA_SUBJECT, title);
        }

        Intent chooser = null;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            PendingIntent pi = PendingIntent.getBroadcast(
                getContext(),
                REQUEST_SHARE,
                new Intent(Intent.EXTRA_CHOSEN_COMPONENT),
                PendingIntent.FLAG_UPDATE_CURRENT
            );
            chooser = Intent.createChooser(intent, dialogTitle, pi.getIntentSender());
            chosenComponent = null;
        } else {
            chooser = Intent.createChooser(intent, dialogTitle);
        }
        chooser.addCategory(Intent.CATEGORY_DEFAULT);
        stopped = false;
        startActivityForResult(call, chooser, REQUEST_SHARE);
    }

    @Override
    protected void handleOnActivityResult(int requestCode, int resultCode, Intent data) {
        PluginCall savedCall = getSavedCall();
        if (savedCall == null) {
            return;
        }
        if (resultCode == Activity.RESULT_CANCELED && !stopped) {
            savedCall.reject("Share canceled");
        } else {
            JSObject result = new JSObject();
            result.put("activityType", chosenComponent != null ? chosenComponent.getPackageName() : "");
            savedCall.resolve(result);
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
