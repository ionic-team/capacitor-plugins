package com.capacitorjs.plugins.device;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;

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

    public long getDiskFree() {
        StatFs statFs = new StatFs(Environment.getRootDirectory().getAbsolutePath());
        return statFs.getAvailableBlocksLong() * statFs.getBlockSizeLong();
    }

    public long getDiskTotal() {
        StatFs statFs = new StatFs(Environment.getRootDirectory().getAbsolutePath());
        return statFs.getBlockCountLong() * statFs.getBlockSizeLong();
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
}
