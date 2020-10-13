package com.capacitorjs.plugins.textzoom;

import android.app.Activity;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class TextZoom {

    Activity activity;
    WebView webView;

    TextZoom(Activity activity, WebView webView) {
        this.activity = activity;
        this.webView = webView;
    }

    public double get() {
        WebSettings settings = webView.getSettings();
        return (double) settings.getTextZoom() / 100;
    }

    public void set(double level) {
        WebSettings settings = webView.getSettings();
        int rounded = (int) Math.round(level * 100);
        settings.setTextZoom(rounded);
    }

    public double getPreferred() {
        return Double.parseDouble(Float.valueOf(activity.getResources().getConfiguration().fontScale).toString());
    }
}
