package com.capacitorjs.plugins.screenreader;

import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

@NativePlugin(name = "ScreenReader")
public class ScreenReaderPlugin extends Plugin {
    public static final String EVENT_SCREEN_READER_STATE_CHANGE = "accessibilityScreenReaderStateChange";
    private ScreenReader implementation;

    @Override
    public void load() {
        implementation = new ScreenReader(getContext());
        implementation.addStateChangeListener(
            enabled -> {
                JSObject ret = new JSObject();
                ret.put("value", enabled);
                notifyListeners(EVENT_SCREEN_READER_STATE_CHANGE, ret);
            }
        );
    }

    @SuppressWarnings("unused")
    @PluginMethod
    public void isEnabled(PluginCall call) {
        JSObject ret = new JSObject();
        ret.put("value", implementation.isEnabled());
        call.success(ret);
    }

    @SuppressWarnings("unused")
    @PluginMethod
    public void speak(PluginCall call) {
        String value = call.getString("value");
        String language = call.getString("language", "en");
        implementation.speak(value, language);
        call.success();
    }
}
