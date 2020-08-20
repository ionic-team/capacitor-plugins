package com.capacitorjs.plugins.geolocation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.PluginRequestCodes;
import java.util.HashMap;
import java.util.Map;

@NativePlugin(
    name = "Geolocation",
    permissions = { Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION },
    permissionRequestCode = PluginRequestCodes.GEOLOCATION_REQUEST_PERMISSIONS
)
public class GeolocationPlugin extends Plugin {
    private Geolocation implementation;

    private Map<String, PluginCall> watchingCalls = new HashMap<>();

    @Override
    public void load() {
        implementation = new Geolocation(getContext());
    }

    @PluginMethod(returnType = PluginMethod.RETURN_CALLBACK)
    public void watchPosition(PluginCall call) {
        call.save();
        if (!hasRequiredPermissions()) {
            saveCall(call);
            pluginRequestAllPermissions();
        } else {
            startWatch(call);
        }
    }

    @PluginMethod
    public void getCurrentPosition(final PluginCall call) {
        if (!hasRequiredPermissions()) {
            saveCall(call);
            pluginRequestAllPermissions();
        } else {
            boolean enableHighAccuracy = call.getBoolean("enableHighAccuracy", false);
            int timeout = call.getInt("timeout", 10000);

            implementation.sendLocation(
                enableHighAccuracy,
                timeout,
                false,
                new LocationResultCallback() {

                    @Override
                    public void success(JSObject location) {
                        call.success(location);
                    }

                    @Override
                    public void error(String message) {
                        call.error(message);
                    }
                }
            );
        }
    }

    @SuppressWarnings("MissingPermission")
    private void startWatch(final PluginCall call) {
        boolean enableHighAccuracy = call.getBoolean("enableHighAccuracy", false);
        int timeout = call.getInt("timeout", 10000);

        implementation.requestLocationUpdates(
            enableHighAccuracy,
            timeout,
            false,
            new LocationResultCallback() {

                @Override
                public void success(JSObject location) {
                    call.success(location);
                }

                @Override
                public void error(String message) {
                    call.error(message);
                }
            }
        );

        watchingCalls.put(call.getCallbackId(), call);
    }

    @SuppressWarnings("MissingPermission")
    @PluginMethod
    public void clearWatch(PluginCall call) {
        String callbackId = call.getString("id");
        if (callbackId != null) {
            PluginCall removed = watchingCalls.remove(callbackId);
            if (removed != null) {
                removed.release(bridge);
            }
        }
        if (watchingCalls.size() == 0) {
            implementation.clearLocationUpdates();
        }
        call.success();
    }

    @Override
    protected void handleRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.handleRequestPermissionsResult(requestCode, permissions, grantResults);

        final PluginCall savedCall = getSavedCall();
        if (savedCall == null) {
            return;
        }

        for (int result : grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) {
                savedCall.error("User denied location permission");
                return;
            }
        }

        if (savedCall.getMethodName().equals("getCurrentPosition")) {
            boolean enableHighAccuracy = savedCall.getBoolean("enableHighAccuracy", false);
            int timeout = savedCall.getInt("timeout", 10000);

            implementation.sendLocation(
                enableHighAccuracy,
                timeout,
                true,
                new LocationResultCallback() {

                    @Override
                    public void success(JSObject location) {
                        savedCall.success(location);
                    }

                    @Override
                    public void error(String message) {
                        savedCall.error(message);
                    }
                }
            );
        } else if (savedCall.getMethodName().equals("watchPosition")) {
            startWatch(savedCall);
        } else {
            savedCall.resolve();
            savedCall.release(bridge);
        }
    }

    /**
     * Process a new location item and send it to any listening calls
     * @param location
     */
    private void processLocation(Location location) {
        for (Map.Entry<String, PluginCall> watch : watchingCalls.entrySet()) {
            watch.getValue().success(implementation.getJSObjectForLocation(location));
        }
    }

    @PluginMethod
    public void echo(PluginCall call) {
        String value = call.getString("value");

        JSObject ret = new JSObject();
        ret.put("value", value);
        call.success(ret);
    }
}
