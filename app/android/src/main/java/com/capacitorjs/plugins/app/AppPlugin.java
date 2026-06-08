package com.capacitorjs.plugins.app;

import static android.window.BackEvent.EDGE_LEFT;
import static android.window.BackEvent.EDGE_NONE;
import static android.window.BackEvent.EDGE_RIGHT;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Build;
import android.window.BackEvent;
import android.window.OnBackAnimationCallback;
import android.window.OnBackInvokedDispatcher;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.pm.PackageInfoCompat;
import androidx.core.os.LocaleListCompat;
import com.getcapacitor.JSObject;
import com.getcapacitor.Logger;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.util.InternalUtils;
import java.util.Locale;

@CapacitorPlugin(name = "App")
public class AppPlugin extends Plugin {

    private static final String EVENT_BACK_BUTTON = "backButton";
    private static final String EVENT_BACK_GESTURE = "edgeGesture";
    private static final String EVENT_URL_OPEN = "appUrlOpen";
    private static final String EVENT_STATE_CHANGE = "appStateChange";
    private static final String EVENT_RESTORED_RESULT = "appRestoredResult";
    private static final String EVENT_PAUSE = "pause";
    private static final String EVENT_RESUME = "resume";
    private boolean hasPausedEver = false;
    private boolean backButtonHandlerEnabled = false;
    private boolean edgeGestureHandlerEnabled = false;

    private OnBackPressedCallback onBackPressedCallback;
    private OnBackAnimationCallback onBackAnimationCallback;

    private String activeEdge = null;
    private Float lastEdgeProgress = null;
    private Float lastEdgeTouchX = null;
    private Float lastEdgeTouchY = null;


    public void load() {
        this.backButtonHandlerEnabled = !getConfig().getBoolean("disableBackButtonHandler", false);
        this.edgeGestureHandlerEnabled = getConfig().getBoolean("enableEdgeGestureHandler", false);

        bridge
            .getApp()
            .setStatusChangeListener((isActive) -> {
                Logger.debug(getLogTag(), "Firing change: " + isActive);
                JSObject data = new JSObject();
                data.put("isActive", isActive);
                notifyListeners(EVENT_STATE_CHANGE, data, false);
            });
        bridge
            .getApp()
            .setAppRestoredListener((result) -> {
                Logger.debug(getLogTag(), "Firing restored result");
                notifyListeners(EVENT_RESTORED_RESULT, result.getWrappedResult(), true);
            });

        this.onBackPressedCallback = new OnBackPressedCallback(backButtonHandlerEnabled && !edgeGestureHandlerEnabled) {
            @Override
            public void handleOnBackPressed() {
                if (!hasListeners(EVENT_BACK_BUTTON)) {
                    if (bridge.getWebView().canGoBack()) {
                        bridge.getWebView().goBack();
                    }
                } else {
                    JSObject data = new JSObject();
                    data.put("canGoBack", bridge.getWebView().canGoBack());
                    notifyListeners(EVENT_BACK_BUTTON, data, true);
                    bridge.triggerJSEvent("backbutton", "document");
                }
            }
        };

        if (this.edgeGestureHandlerEnabled) {
            this.setupBackGestureHandlers();
        }

        getActivity().getOnBackPressedDispatcher().addCallback(getActivity(), this.onBackPressedCallback);
    }

    @PluginMethod
    public void exitApp(PluginCall call) {
        unsetAppListeners();
        call.resolve();
        getBridge().getActivity().finish();
    }

    @PluginMethod
    public void getInfo(PluginCall call) {
        JSObject data = new JSObject();
        try {
            PackageInfo pinfo = InternalUtils.getPackageInfo(getContext().getPackageManager(), getContext().getPackageName());
            ApplicationInfo applicationInfo = getContext().getApplicationInfo();
            int stringId = applicationInfo.labelRes;
            String appName = stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : getContext().getString(stringId);
            data.put("name", appName);
            data.put("id", pinfo.packageName);
            data.put("build", Integer.toString((int) PackageInfoCompat.getLongVersionCode(pinfo)));
            data.put("version", pinfo.versionName);
            call.resolve(data);
        } catch (Exception ex) {
            call.reject("Unable to get App Info");
        }
    }

    @PluginMethod
    public void getLaunchUrl(PluginCall call) {
        Uri launchUri = bridge.getIntentUri();
        if (launchUri != null) {
            JSObject d = new JSObject();
            d.put("url", launchUri.toString());
            call.resolve(d);
        } else {
            call.resolve();
        }
    }

    @PluginMethod
    public void getState(PluginCall call) {
        JSObject data = new JSObject();
        data.put("isActive", this.bridge.getApp().isActive());
        call.resolve(data);
    }

    @PluginMethod
    public void minimizeApp(PluginCall call) {
        getActivity().moveTaskToBack(true);
        call.resolve();
    }

    @PluginMethod
    public void toggleBackButtonHandler(PluginCall call) {
        if (this.onBackPressedCallback == null) {
            call.reject("onBackPressedCallback is not set");
            return;
        }

        Boolean enabled = call.getBoolean("enabled", false);
        backButtonHandlerEnabled = enabled;

        this.onBackPressedCallback.setEnabled(backButtonHandlerEnabled);
        call.resolve();
    }

    @PluginMethod
    public void getAppLanguage(PluginCall call) {
        JSObject ret = new JSObject();
        LocaleListCompat appLocales = AppCompatDelegate.getApplicationLocales();
        Locale appLocale = !appLocales.isEmpty() ? appLocales.get(0) : null;
        ret.put("value", appLocale != null ? appLocale.getLanguage() : Locale.getDefault().getLanguage());
        call.resolve(ret);
    }

    @PluginMethod
    public void toggleEdgeGestureHandler(PluginCall call) {
        Boolean enabled = call.getBoolean("enabled", false);
        edgeGestureHandlerEnabled = enabled;

        if (edgeGestureHandlerEnabled) {
            this.onBackPressedCallback.setEnabled(false);
            setupBackGestureHandlers();
        } else {
            this.onBackPressedCallback.setEnabled(this.backButtonHandlerEnabled);
            teardownBackGestureHandlers();
        }

        call.resolve();
    }

    /**
     * Handle ACTION_VIEW intents to store a URL that was used to open the app
     * @param intent
     */
    @Override
    protected void handleOnNewIntent(Intent intent) {
        super.handleOnNewIntent(intent);
        // read intent
        String action = intent.getAction();
        Uri url = intent.getData();

        if (!Intent.ACTION_VIEW.equals(action) || url == null) {
            return;
        }

        JSObject ret = new JSObject();
        ret.put("url", url.toString());
        notifyListeners(EVENT_URL_OPEN, ret, true);
    }

    @Override
    protected void handleOnPause() {
        super.handleOnPause();
        hasPausedEver = true;
        notifyListeners(EVENT_PAUSE, null);
    }

    @Override
    protected void handleOnResume() {
        super.handleOnResume();
        if (hasPausedEver) {
            notifyListeners(EVENT_RESUME, null);
        }
    }

    @Override
    protected void handleOnDestroy() {
        unsetAppListeners();
    }

    private void unsetAppListeners() {
        bridge.getApp().setStatusChangeListener(null);
        bridge.getApp().setAppRestoredListener(null);
    }

    private void setupBackGestureHandlers() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            this.onBackAnimationCallback = new OnBackAnimationCallback() {
                @Override
                public void onBackInvoked() {
                    if (hasListeners(EVENT_BACK_GESTURE)) {
                        JSObject data = new JSObject();
                        data.put("phase", "commit");
                        data.put("progress", lastEdgeProgress);
                        data.put("touchX", lastEdgeTouchX);
                        data.put("touchY", lastEdgeTouchY);
                        data.put("swipeEdge", activeEdge);

                        notifyListeners(EVENT_BACK_GESTURE, data, true);

                        lastEdgeProgress = null;
                        lastEdgeTouchX = null;
                        lastEdgeTouchY = null;
                        activeEdge = null;
                    }
                }

                @Override
                public void onBackStarted(@NonNull BackEvent backEvent) {
                    if (hasListeners(EVENT_BACK_GESTURE)) {
                        OnBackAnimationCallback.super.onBackStarted(backEvent);
                        JSObject data = new JSObject();
                        data.put("phase", "start");
                        data.put("progress", backEvent.getProgress());
                        data.put("touchX", backEvent.getTouchX());
                        data.put("touchY", backEvent.getTouchY());
                        data.put("swipeEdge", getSwipeEdge(backEvent.getSwipeEdge()));

                        notifyListeners(EVENT_BACK_GESTURE, data, true);

                        lastEdgeProgress = backEvent.getProgress();
                        lastEdgeTouchX = backEvent.getTouchX();
                        lastEdgeTouchY = backEvent.getTouchY();
                        activeEdge = getSwipeEdge(backEvent.getSwipeEdge());
                    }
                }

                @Override
                public void onBackProgressed(@NonNull BackEvent backEvent) {
                    if (hasListeners(EVENT_BACK_GESTURE)) {
                        OnBackAnimationCallback.super.onBackStarted(backEvent);
                        JSObject data = new JSObject();
                        data.put("phase", "progress");
                        data.put("progress", backEvent.getProgress());
                        data.put("touchX", backEvent.getTouchX());
                        data.put("touchY", backEvent.getTouchY());
                        data.put("swipeEdge", getSwipeEdge(backEvent.getSwipeEdge()));

                        notifyListeners(EVENT_BACK_GESTURE, data, true);

                        lastEdgeProgress = backEvent.getProgress();
                        lastEdgeTouchX = backEvent.getTouchX();
                        lastEdgeTouchY = backEvent.getTouchY();
                        activeEdge = getSwipeEdge(backEvent.getSwipeEdge());
                    }
                }

                @Override
                public void onBackCancelled() {
                    if (hasListeners(EVENT_BACK_GESTURE)) {
                        OnBackAnimationCallback.super.onBackCancelled();

                        JSObject data = new JSObject();
                        data.put("phase", "cancel");
                        data.put("progress", lastEdgeProgress);
                        data.put("touchX", lastEdgeTouchX);
                        data.put("touchY", lastEdgeTouchY);
                        data.put("swipeEdge", activeEdge);

                        notifyListeners(EVENT_BACK_GESTURE, data, true);

                        lastEdgeProgress = null;
                        lastEdgeTouchX = null;
                        lastEdgeTouchY = null;
                        activeEdge = null;
                    }
                }
            };

            getActivity().getOnBackInvokedDispatcher().registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT,
                this.onBackAnimationCallback
            );
        }
    }

    private void teardownBackGestureHandlers() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            getActivity().getOnBackInvokedDispatcher().unregisterOnBackInvokedCallback(this.onBackAnimationCallback);
            this.onBackAnimationCallback = null;
        }
    }

    private String getSwipeEdge(int edge) {
        return switch (edge) {
            case EDGE_LEFT -> "left";
            case EDGE_RIGHT -> "right";
            case EDGE_NONE -> "none";
            default -> "none";
        };
    }
}
