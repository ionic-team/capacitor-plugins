package com.capacitorjs.plugins.browser;

import android.graphics.Color;
import android.net.Uri;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Logger;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.PluginRequestCodes;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;

@NativePlugin(name = "Browser", requestCodes = { PluginRequestCodes.BROWSER_OPEN_CHROME_TAB })
public class BrowserPlugin extends Plugin {
    private Browser implementation;

    @PluginMethod
    public void load() {
        implementation = new Browser(getContext());
        implementation.setBrowserEventListener(this::onBrowserEvent);
    }

    @PluginMethod
    public void open(PluginCall call) {
        // get the URL
        String urlString = call.getString("url");
        if (urlString == null) {
            call.reject("Must provide a URL to open");
            return;
        }
        if (urlString.isEmpty()) {
            call.reject("URL must not be empty");
            return;
        }
        Uri url;
        try {
            url = Uri.parse(urlString);
        } catch (Exception ex) {
            call.reject(ex.getLocalizedMessage());
            return;
        }

        // get the toolbar color, if provided
        String colorString = call.getString("toolbarColor");
        Integer toolbarColor = null;
        if (colorString != null) try {
            toolbarColor = Color.parseColor(colorString);
        } catch (IllegalArgumentException ex) {
            Logger.error(getLogTag(), "Invalid color provided for toolbarColor. Using default", null);
        }

        // open the browser and finish
        implementation.open(url, toolbarColor);
        call.resolve();
    }

    @PluginMethod
    public void close(PluginCall call) {
        call.unimplemented();
    }

    @PluginMethod
    public void prefetch(PluginCall call) {
        JSArray urlStrings = call.getArray("urls");
        if (urlStrings == null || urlStrings.length() == 0) {
            call.reject("Must provide an array of URLs to prefetch");
            return;
        }

        if (implementation.isPrepared()) {
            call.reject("Browser session isn't ready yet");
            return;
        }

        List<Uri> urls = new ArrayList<>();
        try {
            for (String urlString : urlStrings.<String>toList()) {
                urls.add(Uri.parse(urlString));
            }
        } catch (JSONException ex) {
            call.reject("Unable to process provided list of urls. Ensure each item is a string and valid URL", ex);
            return;
        }

        implementation.prefetch(urls);
        call.resolve();
    }

    @Override
    protected void handleOnResume() {
        boolean ok = implementation.bindService();
        if (!ok) {
            Logger.error(getLogTag(), "Error binding to custom tabs service", null);
        }
    }

    @Override
    protected void handleOnPause() {
        implementation.unbindService();
    }

    void onBrowserEvent(int event) {
        switch (event) {
            case Browser.BROWSER_LOADED:
                notifyListeners("browserPageLoaded", new JSObject());
                break;
            case Browser.BROWSER_FINISHED:
                notifyListeners("browserFinished", new JSObject());
                break;
        }
    }
}
