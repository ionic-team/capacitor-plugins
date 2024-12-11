package com.capacitorjs.plugins.device;

import android.os.Build;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import java.util.Locale;

@CapacitorPlugin(name = "Device")
public class DevicePlugin extends Plugin {

    private Device implementation;

    @Override
    public void load() {
        implementation = new Device(getContext());
    }

    @PluginMethod
    public void getId(PluginCall call) {
        JSObject r = new JSObject();

        r.put("identifier", implementation.getUuid());

        call.resolve(r);
    }

    @PluginMethod
    public void getInfo(PluginCall call) {
        JSObject r = new JSObject();

        r.put("memUsed", implementation.getMemUsed());
        r.put("model", android.os.Build.MODEL);
        r.put("operatingSystem", "android");
        r.put("osVersion", android.os.Build.VERSION.RELEASE);
        r.put("androidSDKVersion", Build.VERSION.SDK_INT);
        r.put("platform", implementation.getPlatform());
        r.put("manufacturer", android.os.Build.MANUFACTURER);
        r.put("isVirtual", implementation.isVirtual());
        r.put("name", implementation.getName());
        r.put("webViewVersion", implementation.getWebViewVersion());

        call.resolve(r);
    }

    @PluginMethod
    public void getBatteryInfo(PluginCall call) {
        JSObject r = new JSObject();

        r.put("batteryLevel", implementation.getBatteryLevel());
        r.put("isCharging", implementation.isCharging());

        call.resolve(r);
    }

    @PluginMethod
    public void getLanguageCode(PluginCall call) {
        JSObject ret = new JSObject();
        ret.put("value", Locale.getDefault().getLanguage());
        call.resolve(ret);
    }

    @PluginMethod
    public void getLanguageTag(PluginCall call) {
        JSObject ret = new JSObject();
        ret.put("value", Locale.getDefault().toLanguageTag());
        call.resolve(ret);
    }
}
