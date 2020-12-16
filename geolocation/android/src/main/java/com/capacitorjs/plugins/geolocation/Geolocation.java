package com.capacitorjs.plugins.geolocation;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class Geolocation {

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private Context context;

    public Geolocation(Context context) {
        this.context = context;
    }

    public void sendLocation(
        boolean enableHighAccuracy,
        int timeout,
        final boolean getCurrentPosition,
        final LocationResultCallback resultCallback
    ) {
        requestLocationUpdates(enableHighAccuracy, timeout, getCurrentPosition, resultCallback);
    }

    @SuppressWarnings("MissingPermission")
    public void requestLocationUpdates(
        boolean enableHighAccuracy,
        int timeout,
        final boolean getCurrentPosition,
        final LocationResultCallback resultCallback
    ) {
        clearLocationUpdates();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean networkEnabled = false;

        try {
            networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {}

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setMaxWaitTime(timeout);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        int lowPriority = networkEnabled ? LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY : LocationRequest.PRIORITY_LOW_POWER;
        int priority = enableHighAccuracy ? LocationRequest.PRIORITY_HIGH_ACCURACY : lowPriority;
        locationRequest.setPriority(priority);

        locationCallback =
            new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (getCurrentPosition) {
                        clearLocationUpdates();
                    }
                    Location lastLocation = locationResult.getLastLocation();
                    if (lastLocation == null) {
                        resultCallback.error("location unavailable");
                    } else {
                        resultCallback.success(lastLocation);
                    }
                }

                @Override
                public void onLocationAvailability(LocationAvailability availability) {
                    if (!availability.isLocationAvailable()) {
                        resultCallback.error("location unavailable");
                        clearLocationUpdates();
                    }
                }
            };

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    public void clearLocationUpdates() {
        if (locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
            locationCallback = null;
        }
    }
}
