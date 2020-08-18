package com.capacitorjs.plugins.clipboard;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import com.getcapacitor.JSObject;
import com.getcapacitor.Logger;

public class Clipboard {
    private static final String TAG = "Clipboard";

    public enum ContentType {
        STRING,
        IMAGE,
        URL
    }

    private Context context;
    private ClipboardManager clipboard;

    public Clipboard(Context context) {
        this.context = context;
        this.clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
    }

    public void write(String label, ContentType contentType, String content) {
        ClipData data = null;
        switch (contentType) {
            case STRING:
            case IMAGE:
            case URL:
                data = ClipData.newPlainText(label, content);
                break;
        }

        if (data != null && clipboard != null) {
            clipboard.setPrimaryClip(data);
        }
    }

    public ClipboardData read() {
        if (clipboard != null) {
            CharSequence value = null;

            if (clipboard.hasPrimaryClip()) {
                if (clipboard.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                    Logger.debug(TAG, "Got plaintxt");
                    ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
                    value = item.getText();
                } else {
                    Logger.debug(TAG, "Not plaintext!");
                    ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
                    value = item.coerceToText(context).toString();
                }
            }

            ClipboardData clipboardData = new ClipboardData();
            String type = "text/plain";

            if (value != null) {
                clipboardData.setValue(value.toString());
            }

            if (value != null && value.toString().startsWith("data:")) {
                type = value.toString().split(";")[0].split(":")[1];
            }

            clipboardData.setType(type);
            return clipboardData;
        }

        return null;
    }
}
