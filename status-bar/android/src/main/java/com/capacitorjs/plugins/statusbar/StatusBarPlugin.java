package com.capacitorjs.plugins.statusbar;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.util.WebColor;
import java.util.Locale;

@CapacitorPlugin(name = "StatusBar")
public class StatusBarPlugin extends Plugin {

    private StatusBar implementation;

    @Override
    public void load() {
        implementation = new StatusBar(getActivity());
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
                    call.resolve();
                }
            );
    }

    @PluginMethod
    public void getInfo(final PluginCall call) {
        StatusBarInfo info = implementation.getInfo();

        JSObject data = new JSObject();
        data.put("visible", info.isVisible());
        data.put("style", info.getStyle());
        data.put("color", info.getColor());
        data.put("overlays", info.isOverlays());
        call.resolve(data);
    }

    @PluginMethod
    public void setOverlaysWebView(final PluginCall call) {
        final Boolean overlays = call.getBoolean("overlay", true);
        getBridge()
            .executeOnMainThread(
                () -> {
                    implementation.setOverlaysWebView(overlays);
                    call.resolve();
                }
            );
    }
}
