package com.capacitorjs.plugins.statusbar;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class StatusBar {

    public static final String statusBarVisibilityChanged = "statusBarVisibilityChanged";
    public static final String statusBarOverlayChanged = "statusBarOverlayChanged";

    private int currentStatusBarColor;
    private final ChangeListener listener;
    private final AppCompatActivity activity;
    private String currentStyle = "DEFAULT";

    public StatusBar(AppCompatActivity activity, StatusBarConfig config, ChangeListener listener) {
        // save initial color of the status bar
        this.activity = activity;
        this.currentStatusBarColor = getStatusBarColorDeprecated();
        this.listener = listener;
        setBackgroundColor(config.getBackgroundColor());
        setStyle(config.getStyle());
        setOverlaysWebView(config.isOverlaysWebView());
        StatusBarInfo info = getInfo();
        info.setVisible(true);
        listener.onChange(statusBarOverlayChanged, info);
    }

    public void setStyle(String style) {
        Window window = activity.getWindow();
        View decorView = window.getDecorView();
        this.currentStyle = style;
        if (style.equals("DEFAULT")) {
            style = getStyleForTheme();
        }

        WindowInsetsControllerCompat windowInsetsControllerCompat = WindowCompat.getInsetsController(window, decorView);
        windowInsetsControllerCompat.setAppearanceLightStatusBars(!style.equals("DARK"));
    }

    private String getStyleForTheme() {
        int currentNightMode = activity.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode != Configuration.UI_MODE_NIGHT_YES) {
            return "LIGHT";
        }
        return "DARK";
    }

    public void updateStyle() {
        setStyle(this.currentStyle);
    }

    public void setBackgroundColor(int color) {
        Window window = activity.getWindow();
        if (shouldSetStatusBarColor(isEdgeToEdgeOptOutEnabled(window))) {
            clearTranslucentStatusFlagDeprecated();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            setStatusBarColorDeprecated(color);
            currentStatusBarColor = color;

            // only set foreground color if style is default
            if (currentStyle.equals("DEFAULT")) {
                // determine if the color is light or dark using luminance and set icon color
                boolean isLightColor = ColorUtils.calculateLuminance(color) > 0.5;
                WindowInsetsControllerCompat insetsController = WindowCompat.getInsetsController(window, window.getDecorView());
                insetsController.setAppearanceLightStatusBars(isLightColor);
            }
        }
    }

    public void hide() {
        View decorView = activity.getWindow().getDecorView();
        WindowInsetsControllerCompat windowInsetsControllerCompat = WindowCompat.getInsetsController(activity.getWindow(), decorView);
        windowInsetsControllerCompat.hide(WindowInsetsCompat.Type.statusBars());
        StatusBarInfo info = getInfo();
        info.setVisible(false);
        listener.onChange(statusBarVisibilityChanged, info);
    }

    public void show() {
        View decorView = activity.getWindow().getDecorView();
        WindowInsetsControllerCompat windowInsetsControllerCompat = WindowCompat.getInsetsController(activity.getWindow(), decorView);
        windowInsetsControllerCompat.show(WindowInsetsCompat.Type.statusBars());
        StatusBarInfo info = getInfo();
        info.setVisible(true);
        listener.onChange(statusBarVisibilityChanged, info);
    }

    public void setOverlaysWebView(Boolean overlays) {
        View decorView = activity.getWindow().getDecorView();
        int uiOptions = getSystemUiVisibilityDeprecated(decorView);
        if (overlays) {
            // Sets the layout to a fullscreen one that does not hide the actual status bar, so the WebView is displayed behind it.
            uiOptions = uiOptions | getSystemUiFlagLayoutStableDeprecated() | getSystemUiFlagLayoutFullscreenDeprecated();
            setSystemUiVisibilityDeprecated(decorView, uiOptions);
            currentStatusBarColor = getStatusBarColorDeprecated();
            setStatusBarColorDeprecated(Color.TRANSPARENT);
        } else {
            // Sets the layout to a normal one that displays the WebView below the status bar.
            uiOptions = uiOptions & ~getSystemUiFlagLayoutStableDeprecated() & ~getSystemUiFlagLayoutFullscreenDeprecated();
            setSystemUiVisibilityDeprecated(decorView, uiOptions);
            // recover the previous color of the status bar
            setStatusBarColorDeprecated(currentStatusBarColor);
        }
        listener.onChange(statusBarOverlayChanged, getInfo());
    }

    private boolean shouldSetStatusBarColor(boolean hasOptOut) {
        boolean canSetStatusBar;
        int deviceApi = Build.VERSION.SDK_INT;
        if (deviceApi < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            // device below Android 15 - can always set status bar
            canSetStatusBar = true;
        } else if (deviceApi == Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            canSetStatusBar = hasOptOut; // app targets 15 - can set status bar if opted out
        } else {
            canSetStatusBar = false; // app targets 16 - opt-out ignored or app targets 15 but there is not opt out
        }
        return canSetStatusBar;
    }

    private boolean isEdgeToEdgeOptOutEnabled(Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            TypedValue value = new TypedValue();
            window.getContext().getTheme().resolveAttribute(android.R.attr.windowOptOutEdgeToEdgeEnforcement, value, true);
            return value.data != 0; // value is set to -1 on true as of Android 15, so we have to do this.
        }
        return false;
    }

    private boolean getIsOverlaid() {
        return (
            (getSystemUiVisibilityDeprecated(activity.getWindow().getDecorView()) & getSystemUiFlagLayoutFullscreenDeprecated()) ==
            getSystemUiFlagLayoutFullscreenDeprecated()
        );
    }

    public StatusBarInfo getInfo() {
        Window window = activity.getWindow();
        WindowInsetsCompat windowInsetsCompat = ViewCompat.getRootWindowInsets(window.getDecorView());
        boolean isVisible = windowInsetsCompat != null && windowInsetsCompat.isVisible(WindowInsetsCompat.Type.statusBars());
        StatusBarInfo info = new StatusBarInfo();
        info.setStyle(getStyle());
        info.setOverlays(getIsOverlaid());
        info.setVisible(isVisible);
        info.setColor(String.format("#%06X", (0xFFFFFF & getStatusBarColorDeprecated())));
        info.setHeight(getStatusBarHeight());
        return info;
    }

    private String getStyle() {
        View decorView = activity.getWindow().getDecorView();
        String style = "DARK";
        WindowInsetsControllerCompat windowInsetsControllerCompat = WindowCompat.getInsetsController(activity.getWindow(), decorView);
        if (windowInsetsControllerCompat.isAppearanceLightStatusBars()) {
            style = "LIGHT";
        }
        return style;
    }

    private int getStatusBarHeight() {
        DisplayMetrics metrics = activity.getResources().getDisplayMetrics();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsets insets = activity.getWindowManager().getCurrentWindowMetrics().getWindowInsets();
            return (int) (insets.getInsets(WindowInsets.Type.statusBars()).top / metrics.density);
        }

        WindowInsets insets = activity.getWindow().getDecorView().getRootWindowInsets();
        if (insets != null) {
            return getSystemWindowInsetTopDeprecated(insets, metrics);
        }

        // Fallback if the insets are not available
        return 0;
    }

    public interface ChangeListener {
        void onChange(String eventName, StatusBarInfo info);
    }

    @SuppressWarnings("deprecation")
    private int getStatusBarColorDeprecated() {
        return activity.getWindow().getStatusBarColor();
    }

    @SuppressWarnings("deprecation")
    private void setStatusBarColorDeprecated(int color) {
        activity.getWindow().setStatusBarColor(color);
    }

    @SuppressWarnings("deprecation")
    private void clearTranslucentStatusFlagDeprecated() {
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    @SuppressWarnings("deprecation")
    private int getSystemUiVisibilityDeprecated(View decorView) {
        return decorView.getSystemUiVisibility();
    }

    @SuppressWarnings("deprecation")
    private void setSystemUiVisibilityDeprecated(View decorView, int uiOptions) {
        decorView.setSystemUiVisibility(uiOptions);
    }

    @SuppressWarnings("deprecation")
    private int getSystemUiFlagLayoutStableDeprecated() {
        return View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
    }

    @SuppressWarnings("deprecation")
    private int getSystemUiFlagLayoutFullscreenDeprecated() {
        return View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
    }

    @SuppressWarnings("deprecation")
    private int getSystemWindowInsetTopDeprecated(WindowInsets insets, DisplayMetrics metrics) {
        return (int) (insets.getSystemWindowInsetTop() / metrics.density);
    }
}
