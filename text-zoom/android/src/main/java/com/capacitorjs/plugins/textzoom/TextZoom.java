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

    public int get() {
        WebSettings settings = webView.getSettings();
        return settings.getTextZoom();
    }

    public void set(int level) {
        WebSettings settings = webView.getSettings();
        settings.setTextZoom(level);
    }

    public int getPreferred() {
        float fontScale = activity.getResources().getConfiguration().fontScale;
        int preferred = Math.round(fontScale * 100);

        return preferred;
    }
}
