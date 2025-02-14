package com.capacitorjs.plugins.statusbar;

import com.getcapacitor.util.WebColor;

public class StatusBarConfig {

    private boolean overlaysWebView = true;
    private Integer backgroundColor = WebColor.parseColor("#000000");
    private String style = "DEFAULT";

    public boolean isOverlaysWebView() {
        return overlaysWebView;
    }

    public void setOverlaysWebView(boolean overlaysWebView) {
        this.overlaysWebView = overlaysWebView;
    }

    public Integer getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Integer backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }
}
