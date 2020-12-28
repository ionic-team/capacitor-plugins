package com.capacitorjs.plugins.camera;

public enum CameraSource {
    prompt("PROMPT"),
    camera("CAMERA"),
    photos("PHOTOS");

    private String source;

    CameraSource(String source) {
        this.source = source;
    }

    public String getSource() {
        return this.source;
    }
}
