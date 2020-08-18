package com.capacitorjs.plugins.clipboard;

import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

@NativePlugin(name = "Clipboard")
public class ClipboardPlugin extends Plugin {
    private Clipboard implementation;

    @Override
    public void load() {
        implementation = new Clipboard(getContext());
    }

    @PluginMethod
    public void write(PluginCall call) {
        String strVal = call.getString("string");
        String imageVal = call.getString("image");
        String urlVal = call.getString("url");
        String label = call.getString("label");

        if (strVal != null) {
            implementation.write(label, Clipboard.ContentType.STRING, strVal);
        } else if (imageVal != null) {
            implementation.write(label, Clipboard.ContentType.IMAGE, imageVal);
        } else if (urlVal != null) {
            implementation.write(label, Clipboard.ContentType.URL, urlVal);
        }

        call.resolve();
    }

    @PluginMethod
    public void read(PluginCall call) {
        ClipboardData result = implementation.read();

        if (result == null) {
            call.reject("Unable to read clipboard from the given Context");
        } else {
            JSObject resultJS = new JSObject();
            resultJS.put("value", result.getValue());
            resultJS.put("type", result.getType());

            call.resolve(resultJS);
        }
    }
}
