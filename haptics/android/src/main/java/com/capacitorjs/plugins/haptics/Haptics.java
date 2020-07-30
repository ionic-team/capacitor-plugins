package com.capacitorjs.plugins.haptics;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.HapticFeedbackConstants;
import android.webkit.WebView;

public class Haptics {
    private Context context;
    private WebView webView;
    private boolean selectionStarted = false;

    Haptics(Context context, WebView webView) {
        this.context = context;
        this.webView = webView;
    }

    public void vibrate(int duration) {
        if (Build.VERSION.SDK_INT >= 26) {
            ((Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(
                    VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE)
                );
        } else {
            vibratePre26(duration);
        }
    }

    @SuppressWarnings({ "deprecation" })
    private void vibratePre26(int duration) {
        ((Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(duration);
    }

    public void impact() {
        this.webView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
    }

    public void selectionStart() {
        this.selectionStarted = true;
    }

    public void selectionChanged() {
        if (this.selectionStarted) {
            this.webView.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK);
        }
    }

    public void selectionEnd() {
        this.selectionStarted = false;
    }
}
