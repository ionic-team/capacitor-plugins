package com.capacitorjs.plugins.camera;

import android.content.Context;
import android.util.DisplayMetrics;

public class DeviceUtils {
    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) (dp * displayMetrics.density + 0.5f);
    }
}
