package com.capacitorjs.plugins.clipboard;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.content.Intent;

import androidx.core.content.FileProvider;

import com.getcapacitor.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Clipboard {

    private static final String TAG = "Clipboard";

    private final Context context;
    private final ClipboardManager clipboard;

    public Clipboard(Context context) {
        this.context = context;
        this.clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
    }

    /**
     * Writes provided content to the clipboard.
     *
     * @param label   User-visible label for the clip data.
     * @param content The content to be written to the clipboard.
     * @return A response indicating the success status of the write request.
     */
    public ClipboardWriteResponse write(String label, String content) {
        ClipData data;

        if (content.startsWith("content://") || content.startsWith("file://")) {
            Uri uri = Uri.parse(content);
            data = ClipData.newUri(context.getContentResolver(), label, uri);
        } else if (content.startsWith("data:image/")) {
            data = handleBase64Image(label, content);
        } else {
            data = ClipData.newPlainText(label, content);
        }

        return setClipboardData(data);
    }

    private ClipData handleBase64Image(String label, String content) {
        try {
            String base64Image = content.substring(content.indexOf(",") + 1);
            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            File cachePath = new File(context.getCacheDir(), "images");
            if (!cachePath.exists()) {
                cachePath.mkdirs();
            }
            File file = new File(cachePath, "image.png");
            try (FileOutputStream fos = new FileOutputStream(file)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            }

            Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
            context.grantUriPermission(context.getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

            return ClipData.newUri(context.getContentResolver(), label, uri);
        } catch (IOException e) {
            Logger.error(TAG, "Error handling base64 image", e);
            return ClipData.newPlainText(label, content);
        }
    }

    private ClipboardWriteResponse setClipboardData(ClipData data) {
        if (data != null && clipboard != null) {
            try {
                clipboard.setPrimaryClip(data);
                return new ClipboardWriteResponse(true);
            } catch (Exception e) {
                Logger.error(TAG, "Error writing to the clipboard", e);
                return new ClipboardWriteResponse(false, "Writing to the clipboard failed");
            }
        } else if (clipboard == null) {
            return new ClipboardWriteResponse(false, "Problem getting a reference to the system clipboard");
        } else {
            return new ClipboardWriteResponse(false, "Problem formatting data");
        }
    }

    /**
     * Reads data from the clipboard.
     *
     * @return Data from the clipboard or null if no reference to the system clipboard.
     */
    public ClipboardData read() {
        if (clipboard != null && clipboard.hasPrimaryClip()) {
            ClipDescription description = clipboard.getPrimaryClipDescription();
            ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);

            CharSequence value = null;
            Uri uriValue = null;

            if (description.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                Logger.debug(TAG, "Got plaintext");
                value = item.getText();
            } else if (description.hasMimeType(ClipDescription.MIMETYPE_TEXT_URILIST)) {
                Logger.debug(TAG, "Got URI list");
                uriValue = item.getUri();
                value = uriValue.toString();
            } else {
                Logger.debug(TAG, "Not plaintext or URI!");
                value = item.coerceToText(context).toString();
            }

            ClipboardData clipboardData = new ClipboardData();
            String type = "text/plain";

            if (value != null) {
                clipboardData.setValue(value.toString());
            }

            if (uriValue != null) {
                type = "image/*";
            } else if (value != null && value.toString().startsWith("data:")) {
                type = value.toString().split(";")[0].split(":")[1];
            }

            clipboardData.setType(type);
            return clipboardData;
        }

        return null;
    }
}
