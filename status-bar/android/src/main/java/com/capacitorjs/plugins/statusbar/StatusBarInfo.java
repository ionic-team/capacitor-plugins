package com.capacitorjs.plugins.statusbar;

public class StatusBarInfo {

    private boolean overlays;
    private boolean visible;
    private String style;
    private String color;

    public boolean isOverlays() {
        return overlays;
    }

    public void setOverlays(boolean overlays) {
        this.overlays = overlays;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
