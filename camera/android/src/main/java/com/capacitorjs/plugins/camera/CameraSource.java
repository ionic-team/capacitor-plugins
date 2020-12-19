package com.capacitorjs.plugins.camera;

public enum CameraSource {
    prompt("prompt"),
    camera("camera"),
    photos("photos");

    private String source;

    CameraSource(String source) {
        this.source = source;
    }

    public String getSource() {
        return this.source;
    }
}
