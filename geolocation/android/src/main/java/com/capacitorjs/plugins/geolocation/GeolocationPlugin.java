package com.capacitorjs.plugins.geolocation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import java.util.HashMap;
import java.util.Map;

@CapacitorPlugin(
    name = "Geolocation",
    permissions = {
        @Permission(permission = Manifest.permission.ACCESS_COARSE_LOCATION, alias = "location"),
        @Permission(permission = Manifest.permission.ACCESS_FINE_LOCATION, alias = "location")
    },
    permissionRequestCode = GeolocationPlugin.GEOLOCATION_REQUEST_PERMISSIONS
)
public class GeolocationPlugin extends Plugin {

    public static final int GEOLOCATION_REQUEST_PERMISSIONS = 9030;

    private Geolocation implementation;
    private Map<String, PluginCall> watchingCalls = new HashMap<>();

    @Override
    public void load() {
        implementation = new Geolocation(getContext());
    }

    /**
     * Gets a snapshot of the current device position if permissions are granted. If not,
     * the call is rejected in {@link #onRequestPermissionsResult(int, String[], int[])}.
     *
     * @param call Plugin call
     */
    @PluginMethod
    public void getCurrentPosition(final PluginCall call) {
        if (!hasRequiredPermissions()) {
            saveCall(call);
            pluginRequestAllPermissions();
        } else {
            getPosition(call);
        }
    }

    @SuppressWarnings("MissingPermission")
    private void getPosition(PluginCall call) {
        boolean enableHighAccuracy = call.getBoolean("enableHighAccuracy", false);
        int timeout = call.getInt("timeout", 10000);

        implementation.sendLocation(
            enableHighAccuracy,
            timeout,
            false,
            new LocationResultCallback() {
                @Override
                public void success(Location location) {
                    call.resolve(getJSObjectForLocation(location));
                }

                @Override
                public void error(String message) {
                    call.reject(message);
                }
            }
        );
    }

    /**
     * Begins watching for live location changes if permissions are granted. If not,
     * the call is rejected in {@link #onRequestPermissionsResult(int, String[], int[])}.
     *
     * @param call Plugin call
     */
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
                public void success(Location location) {
                    call.resolve(getJSObjectForLocation(location));
                }

                @Override
                public void error(String message) {
                    call.reject(message);
                }
            }
        );

        watchingCalls.put(call.getCallbackId(), call);
    }

    /**
     * Removes an active geolocation watch.
     *
     * @param call Plugin call
     */
    @SuppressWarnings("MissingPermission")
    @PluginMethod
    public void clearWatch(PluginCall call) {
        String callbackId = call.getString("id");

        if (callbackId != null) {
            PluginCall removed = watchingCalls.remove(callbackId);
            if (removed != null) {
                removed.release(bridge);
            }

            if (watchingCalls.size() == 0) {
                implementation.clearLocationUpdates();
            }

            call.resolve();
        } else {
            call.reject("Watch call id must be provided");
        }
    }

    /**
     * Handle permission results related to the plugin. Permission calls may happen by direct
     * plugin call or during a call to {@link #getCurrentPosition} or {@link #watchPosition}.
     *
     * The result of a direct call will return the result states for each permission.
     *
     * @param requestCode The code associated with the permission request {@link #GEOLOCATION_REQUEST_PERMISSIONS}
     * @param permissions The permissions requested
     * @param grantResults The result status
     */
    @Override
    protected void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        PluginCall savedCall = getSavedCall();
        if (savedCall == null) {
            return;
        }

        handlePermissions(permissions, grantResults);

        if (savedCall.getMethodName().equals("getCurrentPosition")) {
            if (!handleDenied(savedCall, grantResults)) {
                boolean enableHighAccuracy = savedCall.getBoolean("enableHighAccuracy", false);
                int timeout = savedCall.getInt("timeout", 10000);

                implementation.sendLocation(
                    enableHighAccuracy,
                    timeout,
                    true,
                    new LocationResultCallback() {
                        @Override
                        public void success(Location location) {
                            savedCall.resolve(getJSObjectForLocation(location));
                        }

                        @Override
                        public void error(String message) {
                            savedCall.reject(message);
                        }
                    }
                );
            }
        } else if (savedCall.getMethodName().equals("watchPosition")) {
            if (!handleDenied(savedCall, grantResults)) {
                startWatch(savedCall);
            }
        } else {
            savedCall.resolve(getPermissionStates());
        }
    }

    /**
     * Return rejection if permissions were denied during a request triggered from a plugin call.
     *
     * @param savedCall The cached plugin call to respond to
     * @param grantResults The plugin permission results
     * @return true if a denied permission occurred and the rejection is handled, false if not
     */
    private boolean handleDenied(PluginCall savedCall, int[] grantResults) {
        for (int result : grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) {
                savedCall.reject("Location permission was denied");
                return true;
            }
        }

        return false;
    }

    private JSObject getJSObjectForLocation(Location location) {
        JSObject ret = new JSObject();
        JSObject coords = new JSObject();
        ret.put("coords", coords);
        ret.put("timestamp", location.getTime());
        coords.put("latitude", location.getLatitude());
        coords.put("longitude", location.getLongitude());
        coords.put("accuracy", location.getAccuracy());
        coords.put("altitude", location.getAltitude());
        if (Build.VERSION.SDK_INT >= 26) {
            coords.put("altitudeAccuracy", location.getVerticalAccuracyMeters());
        }
        coords.put("speed", location.getSpeed());
        coords.put("heading", location.getBearing());
        return ret;
    }
}
