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
import com.getcapacitor.annotation.PermissionCallback;
import java.util.HashMap;
import java.util.Map;

@CapacitorPlugin(
    name = "Geolocation",
    permissions = {
        @Permission(
            strings = { Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION },
            alias = GeolocationPlugin.LOCATION
        ),
        @Permission(strings = { Manifest.permission.ACCESS_COARSE_LOCATION }, alias = GeolocationPlugin.COARSE_LOCATION)
    }
)
public class GeolocationPlugin extends Plugin {

    static final String LOCATION = "location";
    static final String COARSE_LOCATION = "coarseLocation";
    private Geolocation implementation;
    private Map<String, PluginCall> watchingCalls = new HashMap<>();

    @Override
    public void load() {
        implementation = new Geolocation(getContext());
    }

    /**
     * Gets a snapshot of the current device position if permission is granted. The call continues
     * in the {@link #completeCurrentPosition(PluginCall)} method if a permission request is required.
     *
     * @param call Plugin call
     */
    @PluginMethod
    public void getCurrentPosition(final PluginCall call) {
        String alias = getAlias(call);
        if (getPermissionState(alias) != PermissionState.GRANTED) {
            requestPermissionForAlias(alias, call, "completeCurrentPosition");
        } else {
            getPosition(call);
        }
    }

    /**
     * Completes the getCurrentPosition plugin call after a permission request
     * @see #getCurrentPosition(PluginCall)
     * @param call the plugin call
     */
    @PermissionCallback
    private void completeCurrentPosition(PluginCall call) {
        if (getPermissionState(GeolocationPlugin.COARSE_LOCATION) == PermissionState.GRANTED) {
            int timeout = call.getInt("timeout", 10000);

            implementation.sendLocation(
                isHighAccuracy(call),
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
    }

    /**
     * Begins watching for live location changes if permission is granted. The call continues
     * in the {@link #completeWatchPosition(PluginCall)} method if a permission request is required.
     *
     * @param call the plugin call
     */
    @PluginMethod(returnType = PluginMethod.RETURN_CALLBACK)
    public void watchPosition(PluginCall call) {
        call.setKeepAlive(true);
        String alias = getAlias(call);
        if (getPermissionState(alias) != PermissionState.GRANTED) {
            requestPermissionForAlias(alias, call, "completeWatchPosition");
        } else {
            startWatch(call);
        }
    }

    /**
     * Completes the watchPosition plugin call after a permission request
     * @see #watchPosition(PluginCall)
     * @param call the plugin call
     */
    @PermissionCallback
    private void completeWatchPosition(PluginCall call) {
        if (getPermissionState(GeolocationPlugin.COARSE_LOCATION) == PermissionState.GRANTED) {
            startWatch(call);
        } else {
            call.reject("Location permission was denied");
        }
    }

    @SuppressWarnings("MissingPermission")
    private void getPosition(PluginCall call) {
        int timeout = call.getInt("timeout", 10000);
        int maximumAge = call.getInt("maximumAge", 0);
        Location location = implementation.getLastLocation(maximumAge);
        if (location != null) {
            call.resolve(getJSObjectForLocation(location));
        } else {
            implementation.sendLocation(
                isHighAccuracy(call),
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
        }
    }

    @SuppressWarnings("MissingPermission")
    private void startWatch(final PluginCall call) {
        int timeout = call.getInt("timeout", 10000);

        implementation.requestLocationUpdates(
            isHighAccuracy(call),
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

    private String getAlias(PluginCall call) {
        String alias = GeolocationPlugin.LOCATION;
        // TODO replace with Build.VERSION_CODES.S once we target SDK 31
        if (Build.VERSION.SDK_INT >= 31) {
            boolean enableHighAccuracy = call.getBoolean("enableHighAccuracy", false);
            if (!enableHighAccuracy) {
                alias = GeolocationPlugin.COARSE_LOCATION;
            }
        }
        return alias;
    }

    private boolean isHighAccuracy(PluginCall call) {
        boolean enableHighAccuracy = call.getBoolean("enableHighAccuracy", false);
        return enableHighAccuracy && getPermissionState(GeolocationPlugin.LOCATION) == PermissionState.GRANTED;
    }
}
