package com.capacitorjs.plugins.clipboard;

public class ClipboardWriteResponse {

    private boolean success;
    private String errorMessage;

    public ClipboardWriteResponse(boolean success) {
        this(success, "");
    }

    public ClipboardWriteResponse(boolean success, String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
