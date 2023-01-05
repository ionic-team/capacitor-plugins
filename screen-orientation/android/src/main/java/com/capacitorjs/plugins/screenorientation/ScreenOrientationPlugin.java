package com.capacitorjs.plugins.screenorientation;

import android.content.res.Configuration;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "ScreenOrientation")
public class ScreenOrientationPlugin extends Plugin {

    private ScreenOrientation implementation;

    @Override
    public void load() {
        implementation = new ScreenOrientation(getActivity());
    }

    @PluginMethod
    public void orientation(PluginCall call) {
        JSObject ret = new JSObject();
        String type = implementation.getCurrentOrientationType();
        ret.put("type", type);
        call.resolve(ret);
    }

    @PluginMethod
    public void lock(PluginCall call) {
        String orientationType = call.getString("orientation");
        if (orientationType == null) {
            call.reject("Input option 'orientation' must be provided.");
            return;
        }
        implementation.lock(orientationType);
        call.resolve();
    }

    @PluginMethod
    public void unlock(PluginCall call) {
        implementation.unlock();
        call.resolve();
    }

    @Override
    public void handleOnConfigurationChanged(Configuration newConfig) {
        super.handleOnConfigurationChanged(newConfig);
        if (implementation.hasOrientationChanged(newConfig.orientation)) {
            this.onOrientationChanged();
        }
    }

    private void onOrientationChanged() {
        JSObject ret = new JSObject();
        String type = implementation.getCurrentOrientationType();
        ret.put("type", type);
        notifyListeners("screenOrientationChange", ret);
    }
}
