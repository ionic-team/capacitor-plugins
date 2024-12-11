package com.capacitorjs.plugins.device;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Build;
import android.provider.Settings;
import android.webkit.WebView;

public class Device {

    private Context context;

    Device(Context context) {
        this.context = context;
    }

    public long getMemUsed() {
        final Runtime runtime = Runtime.getRuntime();
        final long usedMem = (runtime.totalMemory() - runtime.freeMemory());
        return usedMem;
    }

    public String getPlatform() {
        return "android";
    }

    public String getUuid() {
        return Settings.Secure.getString(this.context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
    }

    public float getBatteryLevel() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = this.context.registerReceiver(null, ifilter);

        int level = -1;
        int scale = -1;

        if (batteryStatus != null) {
            level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        }

        return level / (float) scale;
    }

    public boolean isCharging() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = this.context.registerReceiver(null, ifilter);

        if (batteryStatus != null) {
            int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            return status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
        }
        return false;
    }

    public boolean isVirtual() {
        return android.os.Build.FINGERPRINT.contains("generic") || android.os.Build.PRODUCT.contains("sdk");
    }

    public String getName() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            return Settings.Global.getString(this.context.getContentResolver(), Settings.Global.DEVICE_NAME);
        }

        return null;
    }

    public String getWebViewVersion() {
        PackageInfo info = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            info = WebView.getCurrentWebViewPackage();
        } else {
            try {
                info = getWebViewVersionSubAndroid26();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (info != null) {
            return info.versionName;
        }

        return android.os.Build.VERSION.RELEASE;
    }

    @SuppressWarnings("deprecation")
    private PackageInfo getWebViewVersionSubAndroid26() throws PackageManager.NameNotFoundException {
        String webViewPackage = "com.google.android.webview";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            webViewPackage = "com.android.chrome";
        }
        PackageManager pm = this.context.getPackageManager();
        return pm.getPackageInfo(webViewPackage, 0);
    }
}
