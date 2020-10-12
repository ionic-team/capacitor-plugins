package com.capacitorjs.plugins.clipboard;

public class ClipboardData {

    private String value;
    private String type;

    public ClipboardData() {}

    public ClipboardData(String value, String type) {
        this.value = value;
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public String getType() {
        return type;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setType(String type) {
        this.type = type;
    }
}
