package com.capacitorjs.plugins.geolocation;

import android.Manifest;
import android.location.Location;
import android.os.Build;
import com.getcapacitor.JSObject;
import com.getcapacitor.PermissionState;
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
        @Permission(strings = { Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION }, alias = "location")
    }
)
public class GeolocationPlugin extends Plugin {

    private static final int CURRENT_POSITION_CALLBACK_ID = 1;
    private static final int WATCH_POSITION_CALLBACK_ID = 2;

    private Geolocation implementation;
    private Map<String, PluginCall> watchingCalls = new HashMap<>();

    @Override
    public void load() {
        implementation = new Geolocation(getContext());

        // register the permission callback for getCurrentPosition
        registerPermissionCallback(CURRENT_POSITION_CALLBACK_ID, (call, status) -> {
            if (status.get("location") == PermissionState.GRANTED) {
                boolean enableHighAccuracy = call.getBoolean("enableHighAccuracy", false);
                int timeout = call.getInt("timeout", 10000);

                implementation.sendLocation(
                        enableHighAccuracy,
                        timeout,
                        true,
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
            } else {
                call.reject("Location permission was denied");
            }
        });

        // register the permission callback for watchPosition
        registerPermissionCallback(WATCH_POSITION_CALLBACK_ID, (call, status) -> {
            if (status.get("location") == PermissionState.GRANTED) {
                startWatch(call);
            } else {
                call.reject("Location permission was denied");
            }
        });
    }

    /**
     * Gets a snapshot of the current device position if permissions are granted. If not,
     * the call is rejected in ####.
     *
     * @param call Plugin call
     */
    @PluginMethod
    public void getCurrentPosition(final PluginCall call) {
        if (!hasRequiredPermissions()) {
            requestAllPermissions(call, CURRENT_POSITION_CALLBACK_ID);
        } else {
            getPosition(call);
        }
    }

    /**
     * Begins watching for live location changes if permissions are granted. If not,
     * the call is rejected in ####)}.
     *
     * @param call Plugin call
     */
    @PluginMethod(returnType = PluginMethod.RETURN_CALLBACK)
    public void watchPosition(PluginCall call) {
        call.save();
        if (!hasRequiredPermissions()) {
            requestAllPermissions(call, WATCH_POSITION_CALLBACK_ID);
        } else {
            startWatch(call);
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
