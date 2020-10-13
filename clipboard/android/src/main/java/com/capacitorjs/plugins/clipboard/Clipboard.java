package com.capacitorjs.plugins.clipboard;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import com.getcapacitor.Logger;

public class Clipboard {

    private static final String TAG = "Clipboard";

    private Context context;
    private ClipboardManager clipboard;

    public Clipboard(Context context) {
        this.context = context;
        this.clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
    }

    /**
     * Writes provided content to the clipboard.
     *
     * @param label User-visible label for the clip data.
     * @param content The content to be written to the clipboard.
     * @return A response indicating the success status of the write request.
     */
    public ClipboardWriteResponse write(String label, String content) {
        ClipData data = ClipData.newPlainText(label, content);

        if (data != null && clipboard != null) {
            try {
                clipboard.setPrimaryClip(data);
            } catch (Exception e) {
                Logger.error(TAG, e);
                return new ClipboardWriteResponse(false, "Writing to the clipboard failed");
            }

            return new ClipboardWriteResponse(true);
        } else if (clipboard == null) {
            return new ClipboardWriteResponse(false, "Problem getting a reference to the system clipboard");
        } else {
            return new ClipboardWriteResponse(false, "Problem formatting data");
        }
    }

    /**
     * Reads data from the clipboard.
     * @return Data from the clipboard or null if no reference to the system clipboard.
     */
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
