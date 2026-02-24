package com.capacitorjs.plugins.screenreader;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "ScreenReader")
public class ScreenReaderPlugin extends Plugin {

    public static final String EVENT_STATE_CHANGE = "stateChange";
    private ScreenReader screenReader;

    @Override
    public void load() {
        screenReader = new ScreenReader(getContext());
        screenReader.addStateChangeListener((enabled) -> {
            JSObject ret = new JSObject();
            ret.put("value", enabled);
            notifyListeners(EVENT_STATE_CHANGE, ret);
        });
    }

    @Override
    protected void handleOnDestroy() {
        screenReader.removeAllListeners();
    }

    @SuppressWarnings("unused")
    @PluginMethod
    public void isEnabled(PluginCall call) {
        JSObject ret = new JSObject();
        ret.put("value", screenReader.isEnabled());
        call.resolve(ret);
    }

    @SuppressWarnings("unused")
    @PluginMethod
    public void speak(PluginCall call) {
        String value = call.getString("value");
        String language = call.getString("language", "en");
        screenReader.speak(value, language);
        call.resolve();
    }
}
