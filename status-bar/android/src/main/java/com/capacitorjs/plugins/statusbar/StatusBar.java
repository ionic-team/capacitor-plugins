package com.capacitorjs.plugins.statusbar;

import android.graphics.Color;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class StatusBar {

    private int currentStatusbarColor;
    private AppCompatActivity activity;
    private String defaultStyle;

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

        WindowInsetsControllerCompat windowInsetsControllerCompat = ViewCompat.getWindowInsetsController(decorView);
        if (windowInsetsControllerCompat != null) {
            windowInsetsControllerCompat.setAppearanceLightStatusBars(!style.equals("DARK"));
        }
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
        WindowInsetsControllerCompat windowInsetsControllerCompat = ViewCompat.getWindowInsetsController(decorView);
        if (windowInsetsControllerCompat != null) {
            windowInsetsControllerCompat.hide(WindowInsetsCompat.Type.statusBars());
        }
    }

    public void show() {
        View decorView = activity.getWindow().getDecorView();
        WindowInsetsControllerCompat windowInsetsControllerCompat = ViewCompat.getWindowInsetsController(decorView);
        if (windowInsetsControllerCompat != null) {
            windowInsetsControllerCompat.show(WindowInsetsCompat.Type.statusBars());
        }
    }

    @SuppressWarnings("deprecation")
    public void setOverlaysWebView(Boolean overlays) {
        View decorView = activity.getWindow().getDecorView();
        int uiOptions = decorView.getSystemUiVisibility();
        if (overlays) {
            // Sets the layout to a fullscreen one that does not hide the actual status bar, so the webview is displayed behind it.
            uiOptions = uiOptions | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
            currentStatusbarColor = activity.getWindow().getStatusBarColor();
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else {
            // Sets the layout to a normal one that displays the webview below the status bar.
            uiOptions = uiOptions & ~View.SYSTEM_UI_FLAG_LAYOUT_STABLE & ~View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
            // recover the previous color of the status bar
            activity.getWindow().setStatusBarColor(currentStatusbarColor);
        }
    }

    @SuppressWarnings("deprecation")
    private boolean getIsOverlayed() {
        return (
            (activity.getWindow().getDecorView().getSystemUiVisibility() & View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN) ==
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
    }

    public StatusBarInfo getInfo() {
        Window window = activity.getWindow();
        WindowInsetsCompat windowInsetsCompat = ViewCompat.getRootWindowInsets(window.getDecorView());
        boolean isVisible = windowInsetsCompat != null && windowInsetsCompat.isVisible(WindowInsetsCompat.Type.statusBars());
        StatusBarInfo info = new StatusBarInfo();
        info.setStyle(getStyle());
        info.setOverlays(getIsOverlayed());
        info.setVisible(isVisible);
        info.setColor(String.format("#%06X", (0xFFFFFF & window.getStatusBarColor())));
        return info;
    }

    private String getStyle() {
        View decorView = activity.getWindow().getDecorView();
        String style;
        WindowInsetsControllerCompat windowInsetsControllerCompat = ViewCompat.getWindowInsetsController(decorView);
        if (windowInsetsControllerCompat != null && windowInsetsControllerCompat.isAppearanceLightStatusBars()) {
            style = "LIGHT";
        } else {
            style = "DARK";
        }
        return style;
    }
}
