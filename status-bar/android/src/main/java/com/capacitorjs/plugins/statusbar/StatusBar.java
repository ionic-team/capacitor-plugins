package com.capacitorjs.plugins.statusbar;

import android.graphics.Color;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

public class StatusBar {

    private int currentStatusbarColor;
    private AppCompatActivity activity;
    private String defaultStyle;
    private boolean isOverlayed = false;

    public StatusBar(AppCompatActivity activity) {
        // save initial color of the status bar
        this.activity = activity;
        this.currentStatusbarColor = activity.getWindow().getStatusBarColor();
        this.defaultStyle = getStyle();
    }

    public void setStyle(String style) {
        Window window = activity.getWindow();
        View decorView = window.getDecorView();

        if (style.equals("DEFAULT")) {
            style = this.defaultStyle;
        }

        ViewCompat.getWindowInsetsController(decorView).setAppearanceLightStatusBars(!style.equals("DARK"));
    }

    @SuppressWarnings("deprecation")
    public void setBackgroundColor(int color) {
        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
        // update the local color field as well
        currentStatusbarColor = color;
    }

    public void hide() {
        View decorView = activity.getWindow().getDecorView();
        ViewCompat.getWindowInsetsController(decorView).hide(WindowInsetsCompat.Type.statusBars());
    }

    public void show() {
        View decorView = activity.getWindow().getDecorView();
        ViewCompat.getWindowInsetsController(decorView).show(WindowInsetsCompat.Type.statusBars());
    }

    public void setOverlaysWebView(Boolean overlays) {
        Window window = activity.getWindow();
        if (overlays) {
            // Sets the layout to a fullscreen one that does not hide the actual status bar, so the webview is displayed behind it.
            WindowCompat.setDecorFitsSystemWindows(window, false);
            currentStatusbarColor = window.getStatusBarColor();
            window.setStatusBarColor(Color.TRANSPARENT);
            isOverlayed = true;
        } else {
            WindowCompat.setDecorFitsSystemWindows(window, true);
            window.setStatusBarColor(currentStatusbarColor);
            isOverlayed = false;
        }
    }

    public StatusBarInfo getInfo() {
        Window window = activity.getWindow();
        StatusBarInfo info = new StatusBarInfo();
        info.setStyle(getStyle());
        info.setOverlays(isOverlayed);
        info.setVisible(ViewCompat.getRootWindowInsets(window.getDecorView()).isVisible(WindowInsetsCompat.Type.statusBars()));
        info.setColor(String.format("#%06X", (0xFFFFFF & window.getStatusBarColor())));
        return info;
    }

    private String getStyle() {
        View decorView = activity.getWindow().getDecorView();
        String style;
        if (ViewCompat.getWindowInsetsController(decorView).isAppearanceLightStatusBars()) {
            style = "LIGHT";
        } else {
            style = "DARK";
        }
        return style;
    }
}
