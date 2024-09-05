package com.capacitorjs.plugins.statusbar;

import com.getcapacitor.JSObject;
import com.getcapacitor.Logger;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.util.WebColor;
import java.util.Locale;

@CapacitorPlugin(name = "StatusBar")
public class StatusBarPlugin extends Plugin {

    public static final String statusBarVisibilityChanged = "statusBarVisibilityChanged";
    public static final String statusBarOverlayChanged = "statusBarOverlayChanged";

    private StatusBar implementation;

    @Override
    public void load() {
        StatusBarConfig config = getStatusBarConfig();
        implementation = new StatusBar(getActivity(), config);
    }

    private StatusBarConfig getStatusBarConfig() {
        StatusBarConfig config = new StatusBarConfig();
        String backgroundColor = getConfig().getString("backgroundColor");
        if (backgroundColor != null) {
            try {
                config.setBackgroundColor(WebColor.parseColor(backgroundColor));
            } catch (IllegalArgumentException ex) {
                Logger.debug("Background color not applied");
            }
        }
        config.setStyle(styleFromConfig(getConfig().getString("style", config.getStyle())));
        config.setOverlaysWebView(getConfig().getBoolean("overlaysWebView", config.isOverlaysWebView()));
        return config;
    }

    private String styleFromConfig(String style) {
        switch (style.toLowerCase()) {
            case "lightcontent":
            case "dark":
                return "DARK";
            case "darkcontent":
            case "light":
                return "LIGHT";
            case "default":
            default:
                return "DEFAULT";
        }
    }

    @PluginMethod
    public void setStyle(final PluginCall call) {
        final String style = call.getString("style");
        if (style == null) {
            call.reject("Style must be provided");
            return;
        }

        getBridge()
            .executeOnMainThread(
                () -> {
                    implementation.setStyle(style);
                    call.resolve();
                }
            );
    }

    @PluginMethod
    public void setBackgroundColor(final PluginCall call) {
        final String color = call.getString("color");
        if (color == null) {
            call.reject("Color must be provided");
            return;
        }

        getBridge()
            .executeOnMainThread(
                () -> {
                    try {
                        final int parsedColor = WebColor.parseColor(color.toUpperCase(Locale.ROOT));
                        implementation.setBackgroundColor(parsedColor);
                        call.resolve();
                    } catch (IllegalArgumentException ex) {
                        call.reject("Invalid color provided. Must be a hex string (ex: #ff0000");
                    }
                }
            );
    }

    @PluginMethod
    public void hide(final PluginCall call) {
        // Hide the status bar.
        getBridge()
            .executeOnMainThread(
                () -> {
                    implementation.hide();
                    StatusBarInfo info = implementation.getInfo();
                    notifyListeners(statusBarVisibilityChanged, toJSObject(info));
                    call.resolve();
                }
            );
    }

    @PluginMethod
    public void show(final PluginCall call) {
        // Show the status bar.
        getBridge()
            .executeOnMainThread(
                () -> {
                    implementation.show();
                    StatusBarInfo info = implementation.getInfo();
                    notifyListeners(statusBarVisibilityChanged, toJSObject(info));
                    call.resolve();
                }
            );
    }

    @PluginMethod
    public void getInfo(final PluginCall call) {
        StatusBarInfo info = implementation.getInfo();
        call.resolve(toJSObject(info));
    }

    @PluginMethod
    public void setOverlaysWebView(final PluginCall call) {
        final Boolean overlays = call.getBoolean("overlay", true);
        getBridge()
            .executeOnMainThread(
                () -> {
                    implementation.setOverlaysWebView(overlays);
                    StatusBarInfo info = implementation.getInfo();
                    notifyListeners(statusBarOverlayChanged, toJSObject(info));
                    call.resolve();
                }
            );
    }

    private JSObject toJSObject(StatusBarInfo info) {
        JSObject data = new JSObject();
        data.put("visible", info.isVisible());
        data.put("style", info.getStyle());
        data.put("color", info.getColor());
        data.put("overlays", info.isOverlays());
        data.put("height", info.getHeight());
        return data;
    }
}
