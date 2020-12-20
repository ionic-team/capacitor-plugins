package com.capacitorjs.plugins.browser;

import android.net.Uri;
import com.getcapacitor.Logger;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.util.WebColor;

@CapacitorPlugin(name = "Browser")
public class BrowserPlugin extends Plugin {

    private Browser implementation;

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
            toolbarColor = WebColor.parseColor(colorString);
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

    @Override
    protected void handleOnResume() {
        if (!implementation.bindService()) {
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
                notifyListeners("browserPageLoaded", null);
                break;
            case Browser.BROWSER_FINISHED:
                notifyListeners("browserFinished", null);
                break;
        }
    }
}
