package com.capacitorjs.plugins.haptics;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import com.capacitorjs.plugins.haptics.arguments.HapticsImpactType;
import com.capacitorjs.plugins.haptics.arguments.HapticsNotificationType;
import com.capacitorjs.plugins.haptics.arguments.HapticsSelectionType;
import com.capacitorjs.plugins.haptics.arguments.HapticsVibrationType;

public class Haptics {
    private Context context;
    private boolean selectionStarted = false;
    private final Vibrator mVibrator;

    Haptics(Context context) {
        this.context = context;
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void vibrate(int duration) {
        if (Build.VERSION.SDK_INT >= 26) {
            mVibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibratePre26(duration);
        }
    }

    @SuppressWarnings({ "deprecation" })
    private void vibratePre26(int duration) {
        mVibrator.vibrate(duration);
    }

    @SuppressWarnings({ "deprecation" })
    private void vibratePre26(long[] pattern, int repeat) {
        mVibrator.vibrate(pattern, repeat);
    }

    public void impact(String style) {
        this.performHaptics(HapticsImpactType.fromString(style));
    }

    public void notification(String type) {
        this.performHaptics(HapticsNotificationType.fromString(type));
    }

    public void selectionStart() {
        this.selectionStarted = true;
    }

    public void selectionChanged() {
        if (this.selectionStarted) {
            performHaptics(new HapticsSelectionType());
        }
    }

    public void selectionEnd() {
        this.selectionStarted = false;
    }

    private void performHaptics(HapticsVibrationType type) {
        if (Build.VERSION.SDK_INT >= 26) {
            mVibrator.vibrate(VibrationEffect.createWaveform(type.getTimings(), type.getAmplitudes(), -1));
        } else {
            vibratePre26(type.getOldSDKPattern(), -1);
        }
    }
}
