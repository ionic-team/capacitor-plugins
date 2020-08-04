package com.capacitorjs.plugins.screenreader;

import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

@NativePlugin(name = "ScreenReader")
public class ScreenReaderPlugin extends Plugin {
    public static final String EVENT_SCREEN_READER_STATE_CHANGE = "screenReaderStateChange";
    private ScreenReader sr;

    @Override
    public void load() {
        sr = new ScreenReader(getContext());
        sr.addStateChangeListener(
            enabled -> {
                JSObject ret = new JSObject();
                ret.put("value", enabled);
                notifyListeners(EVENT_SCREEN_READER_STATE_CHANGE, ret);
            }
        );
    }

    @Override
    protected void handleOnDestroy() {
        sr.removeAllListeners();
    }

    @SuppressWarnings("unused")
    @PluginMethod
    public void isEnabled(PluginCall call) {
        JSObject ret = new JSObject();
        ret.put("value", sr.isEnabled());
        call.success(ret);
    }

    @SuppressWarnings("unused")
    @PluginMethod
    public void speak(PluginCall call) {
        String value = call.getString("value");
        String language = call.getString("language", "en");
        sr.speak(value, language);
        call.success();
    }
}
