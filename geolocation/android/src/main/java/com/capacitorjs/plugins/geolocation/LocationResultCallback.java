package com.capacitorjs.plugins.geolocation;

import com.getcapacitor.JSObject;

public interface LocationResultCallback {
    void success(JSObject location);
    void error(String message);
}
