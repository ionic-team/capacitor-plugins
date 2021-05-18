package com.capacitorjs.plugins.toast;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;

public class Toast {

    private static final int GRAVITY_TOP = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
    private static final int GRAVITY_CENTER = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;

    public static void show(Context c, String text) {
        show(c, text, android.widget.Toast.LENGTH_LONG);
    }

    public static void show(final Context c, final String text, final int duration) {
        show(c, text, duration, "bottom");
    }

    public static void show(final Context c, final String text, final int duration, final String position) {
        new Handler(Looper.getMainLooper())
            .post(
                () -> {
                    android.widget.Toast toast = android.widget.Toast.makeText(c, text, duration);
                    if ("top".equals(position)) {
                        toast.setGravity(GRAVITY_TOP, 0, 40);
                    } else if ("center".equals(position)) {
                        toast.setGravity(GRAVITY_CENTER, 0, 0);
                    }
                    toast.show();
                }
            );
    }
}
