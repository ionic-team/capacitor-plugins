package com.capacitorjs.plugins.textzoom;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.webkit.WebView;
import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

@NativePlugin(name = "TextZoom")
public class TextZoomPlugin extends Plugin {
    private TextZoom tz;
    private Handler mainHandler;

    @Override
    public void load() {
        Activity activity = getBridge().getActivity();
        WebView webView = getBridge().getWebView();
        tz = new TextZoom(activity, webView);
        mainHandler = new Handler(Looper.getMainLooper());
    }

    @PluginMethod
    public void get(final PluginCall call) {
        mainHandler.post(
            () -> {
                JSObject ret = new JSObject();
                ret.put("value", tz.get());
                call.resolve(ret);
            }
        );
    }

    @PluginMethod
    public void set(final PluginCall call) {
        mainHandler.post(
            () -> {
                Integer value = call.getInt("value");

                if (value == null) {
                    call.reject("Invalid integer value.");
                } else {
                    tz.set(value);
                    call.resolve();
                }
            }
        );
    }

    @PluginMethod
    public void getPreferred(final PluginCall call) {
        mainHandler.post(
            () -> {
                JSObject ret = new JSObject();
                ret.put("value", tz.getPreferred());
                call.resolve(ret);
            }
        );
    }
}
