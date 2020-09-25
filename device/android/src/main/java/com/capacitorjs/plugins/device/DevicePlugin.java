package com.capacitorjs.plugins.device;

import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import java.util.Locale;

@NativePlugin(name = "Device")
public class DevicePlugin extends Plugin {
    private Device implementation;

    @Override
    public void load() {
        implementation = new Device(getContext());
    }

    @PluginMethod
    public void getInfo(PluginCall call) {
        JSObject r = new JSObject();

        r.put("memUsed", implementation.getMemUsed());
        r.put("diskFree", implementation.getDiskFree());
        r.put("diskTotal", implementation.getDiskTotal());
        r.put("model", android.os.Build.MODEL);
        r.put("operatingSystem", "android");
        r.put("osVersion", android.os.Build.VERSION.RELEASE);
        r.put("platform", implementation.getPlatform());
        r.put("manufacturer", android.os.Build.MANUFACTURER);
        r.put("uuid", implementation.getUuid());
        r.put("isVirtual", implementation.isVirtual());

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
}
