package com.capacitorjs.plugins.geolocation;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;

import androidx.core.location.LocationManagerCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

class LocationUpdateListener implements LocationListener {

    private final LocationResultCallback callback;

    public LocationUpdateListener(LocationResultCallback callback) {

        this.callback = callback;
    }

    @Override
    public void onLocationChanged(Location location) {
        this.callback.success(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }
}

public class Geolocation {

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private Context context;

    public Geolocation(Context context) {
        this.context = context;
    }

    public Boolean isLocationServicesEnabled() {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return LocationManagerCompat.isLocationEnabled(lm);
    }

    private int getPriority(boolean enableHighAccuracy) {
        boolean networkEnabled = false;
        if (enableHighAccuracy) {
            return Priority.PRIORITY_HIGH_ACCURACY;
        }
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }
        return networkEnabled ? Priority.PRIORITY_BALANCED_POWER_ACCURACY : Priority.PRIORITY_LOW_POWER;
    }

    private boolean hasPlayServices() {
        return GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS;
    }

    /**
     * If the play services are disabled, the location manager will be queryied to indicate what providers are available.
     * Typical providers are `passive`, `network`, `fused` and `gps`
     *
     * This method selects the best appropriate
     */
    private String getPreferredProvider(boolean enableHighAccuracy) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        var providers = lm.getProviders(true);
        if(enableHighAccuracy) {
            if(providers.contains("gps")){
                return "gps";
            }
            if(providers.contains("fused")){
                return "fused";
            }
            if(providers.contains("network")){
                return "network";
            }
        }else{
            if(providers.contains("network")){
                return "network";
            }
            if(providers.contains("fused")){
                return "fused";
            }
            if(providers.contains("gps")){
                return "gps";
            }
        }
        if(providers.contains("passive")){
            return "passive";
        }

        return null;
    }
    @SuppressWarnings("MissingPermission")
    public void sendLocation(boolean enableHighAccuracy, final LocationResultCallback resultCallback) {
        if (!this.isLocationServicesEnabled()) {
            resultCallback.error("location disabled");
            return;
        }

        if (hasPlayServices()) {

            LocationServices
                    .getFusedLocationProviderClient(context)
                    .getCurrentLocation(getPriority(enableHighAccuracy), null)
                    .addOnFailureListener(e -> resultCallback.error(e.getMessage()))
                    .addOnSuccessListener(
                            location -> {
                                if (location == null) {
                                    resultCallback.error("location unavailable");
                                } else {
                                    resultCallback.success(location);
                                }
                            }
                    );
        } else {
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            var provider = getPreferredProvider(enableHighAccuracy);
            if (provider == null) {
                resultCallback.error("Location unavaible: no providers defined. Note: Google Play Services are not available as well");
                return;
            }
            lm.requestLocationUpdates(provider, 1000, 10, new LocationUpdateListener(resultCallback));
        }


    }

    @SuppressWarnings("MissingPermission")
    public void requestLocationUpdates(boolean enableHighAccuracy, int timeout, final LocationResultCallback resultCallback) {


        if (!this.isLocationServicesEnabled()) {
            resultCallback.error("location disabled");
            return;
        }

        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (hasPlayServices()) {
            clearLocationUpdates();
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

            LocationRequest locationRequest = new LocationRequest.Builder(10000)
                    .setMaxUpdateDelayMillis(timeout)
                    .setMinUpdateIntervalMillis(5000)
                    .setPriority(getPriority(enableHighAccuracy))
                    .build();

            locationCallback =
                    new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            Location lastLocation = locationResult.getLastLocation();
                            if (lastLocation == null) {
                                resultCallback.error("location unavailable");
                            } else {
                                resultCallback.success(lastLocation);
                            }
                        }
                    };

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        } else {
            var provider = getPreferredProvider(enableHighAccuracy);
            if (provider == null) {
                resultCallback.error("Location unavaible: no providers defined. Note: Google Play Services are not available as well");
                return;
            }
            lm.requestLocationUpdates(provider, 1000, 10, new LocationUpdateListener(resultCallback));

        }
    }

    public void clearLocationUpdates() {
        if (locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
            locationCallback = null;
        }
    }

    @SuppressWarnings("MissingPermission")
    public Location getLastLocation(int maximumAge) {
        Location lastLoc = null;
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        for (String provider : lm.getAllProviders()) {
            Location tmpLoc = lm.getLastKnownLocation(provider);
            if (tmpLoc != null) {
                long locationAge = SystemClock.elapsedRealtimeNanos() - tmpLoc.getElapsedRealtimeNanos();
                long maximumAgeNanoSec = maximumAge * 1000000L;
                if (
                        locationAge <= maximumAgeNanoSec &&
                                (lastLoc == null || lastLoc.getElapsedRealtimeNanos() > tmpLoc.getElapsedRealtimeNanos())
                ) {
                    lastLoc = tmpLoc;
                }
            }
        }
        return lastLoc;
    }
}
