package com.capacitorjs.plugins.device;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import java.util.Locale;

@CapacitorPlugin(name = "Device")
public class DevicePlugin extends Plugin {

    public static final String BATTERY_CHARGING_STATE_CHANGE_EVENT = "batteryChargingStateChange";

    private Device implementation;
    private Boolean lastBatteryChargingState;

    private final BroadcastReceiver batteryStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || !Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                return;
            }
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean charging =
                status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
            if (lastBatteryChargingState == null) {
                lastBatteryChargingState = charging;
                return;
            }
            if (lastBatteryChargingState != charging) {
                lastBatteryChargingState = charging;
                notifyBatteryChargingStateChange(intent);
            }
        }
    };

    @Override
    public void load() {
        implementation = new Device(getContext());
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getContext().registerReceiver(batteryStateReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            getContext().registerReceiver(batteryStateReceiver, filter);
        }
    }

    @Override
    protected void handleOnDestroy() {
        try {
            getContext().unregisterReceiver(batteryStateReceiver);
        } catch (IllegalArgumentException e) {
            // Receiver was not registered
        }
    }

    private void notifyBatteryChargingStateChange(Intent batteryIntent) {
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryLevel = level >= 0 && scale > 0 ? level / (float) scale : -1;
        int status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging =
            status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;

        JSObject data = new JSObject();
        data.put("batteryLevel", batteryLevel);
        data.put("isCharging", isCharging);
        notifyListeners(BATTERY_CHARGING_STATE_CHANGE_EVENT, data);
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
        r.put("model", Build.MODEL);
        r.put("operatingSystem", "android");
        r.put("osVersion", Build.VERSION.RELEASE);
        r.put("androidSDKVersion", Build.VERSION.SDK_INT);
        r.put("platform", implementation.getPlatform());
        r.put("manufacturer", Build.MANUFACTURER);
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
