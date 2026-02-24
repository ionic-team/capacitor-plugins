package com.capacitorjs.plugins.textzoom;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.webkit.WebView;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "TextZoom")
public class TextZoomPlugin extends Plugin {

    private TextZoom textZoom;
    private Handler mainHandler;

    @Override
    public void load() {
        Activity activity = getBridge().getActivity();
        WebView webView = getBridge().getWebView();
        textZoom = new TextZoom(activity, webView);
        mainHandler = new Handler(Looper.getMainLooper());
    }

    @PluginMethod
    public void get(final PluginCall call) {
        mainHandler.post(() -> {
            JSObject ret = new JSObject();
            ret.put("value", textZoom.get());
            call.resolve(ret);
        });
    }

    @PluginMethod
    public void set(final PluginCall call) {
        mainHandler.post(() -> {
            Double value = call.getDouble("value");

            if (value == null) {
                call.reject("Invalid integer value.");
            } else {
                textZoom.set(value);
                call.resolve();
            }
        });
    }

    @PluginMethod
    public void getPreferred(final PluginCall call) {
        JSObject ret = new JSObject();
        ret.put("value", textZoom.getPreferred());
        call.resolve(ret);
    }
}
