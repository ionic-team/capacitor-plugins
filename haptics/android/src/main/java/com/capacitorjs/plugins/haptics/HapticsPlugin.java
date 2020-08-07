package com.capacitorjs.plugins.haptics;

import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

@NativePlugin(name = "Haptics")
public class HapticsPlugin extends Plugin {
    private Haptics implementation;

    @Override
    public void load() {
        implementation = new Haptics(getContext());
    }

    @PluginMethod
    public void vibrate(PluginCall call) {
        int duration = call.getInt("duration", 300);
        implementation.vibrate(duration);
        call.resolve();
    }

    @PluginMethod
    public void impact(PluginCall call) {
        implementation.impact(call.getString("style"));
        call.resolve();
    }

    @PluginMethod
    public void notification(PluginCall call) {
        implementation.notification(call.getString("type"));
        call.resolve();
    }

    @PluginMethod
    public void selectionStart(PluginCall call) {
        implementation.selectionStart();
        call.resolve();
    }

    @PluginMethod
    public void selectionChanged(PluginCall call) {
        implementation.selectionChanged();
        call.resolve();
    }

    @PluginMethod
    public void selectionEnd(PluginCall call) {
        implementation.selectionEnd();
        call.resolve();
    }
}
