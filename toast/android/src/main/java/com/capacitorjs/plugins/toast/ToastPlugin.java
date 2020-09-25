package com.capacitorjs.plugins.toast;

import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

@NativePlugin(name = "Toast")
public class ToastPlugin extends Plugin {

    @PluginMethod
    public void show(PluginCall call) {
        String text = call.getString("text");
        if (text == null) {
            call.reject("Must provide text");
            return;
        }

        String durationType = call.getString("duration", "short");

        int duration = android.widget.Toast.LENGTH_SHORT;
        if ("long".equals(durationType)) {
            duration = android.widget.Toast.LENGTH_LONG;
        }
        String position = call.getString("position", "bottom");
        Toast.show(getContext(), text, duration, position);

        call.resolve();
    }
}
