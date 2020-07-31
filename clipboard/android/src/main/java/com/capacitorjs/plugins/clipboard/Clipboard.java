package com.capacitorjs.plugins.clipboard;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import com.getcapacitor.JSObject;
import com.getcapacitor.Logger;

public class Clipboard {

    public enum ContentType {
        STRING,
        IMAGE,
        URL
    }

    private Context context;
    private String logTag = Logger.tags(this.getClass().getSimpleName());

    public Clipboard(Context context) {
        this.context = context;
    }

    public void write(String label, ContentType contentType, String content) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

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

    public JSObject read() {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

        if (clipboard != null) {
            CharSequence value = null;

            if (clipboard.hasPrimaryClip()) {
                if (clipboard.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                    Logger.debug(logTag, "Got plaintxt");
                    ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
                    value = item.getText();
                } else {
                    Logger.debug(logTag, "Not plaintext!");
                    ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
                    value = item.coerceToText(context).toString();
                }
            }

            JSObject ret = new JSObject();
            String type = "text/plain";
            ret.put("value", value != null ? value : "");

            if (value != null && value.toString().startsWith("data:")) {
                type = value.toString().split(";")[0].split(":")[1];
            }

            ret.put("type", type);

            return ret;
        }

        return null;
    }
}
