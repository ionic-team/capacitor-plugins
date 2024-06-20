package com.capacitorjs.plugins.clipboard;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "Clipboard")
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

        ClipboardWriteResponse response;

        if (strVal != null) {
            response = implementation.write(label, strVal);
        } else if (imageVal != null) {
            response = implementation.write(label, imageVal);
        } else if (urlVal != null) {
            response = implementation.write(label, urlVal);
        } else {
            call.reject("No data provided");
            return;
        }

        if (response.isSuccess()) {
            call.resolve();
        } else {
            call.reject(response.getErrorMessage());
        }
    }

    @PluginMethod
    public void read(PluginCall call) {
        ClipboardData result = implementation.read();
        if (result != null) {
            JSObject ret = new JSObject();
            ret.put("value", result.getValue());
            ret.put("type", result.getType());
            call.resolve(ret);
        } else {
            call.reject("No data on clipboard");
        }
    }
}
