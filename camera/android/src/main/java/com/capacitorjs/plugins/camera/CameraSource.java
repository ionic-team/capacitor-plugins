package com.capacitorjs.plugins.camera;

public enum CameraSource {
    PROMPT("PROMPT"),
    CAMERA("CAMERA"),
    CAMERA_MULTI("CAMERA_MULTI"),
    PHOTOS("PHOTOS");

    private String source;

    CameraSource(String source) {
        this.source = source;
    }

    public String getSource() {
        return this.source;
    }
}
