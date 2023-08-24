package com.capacitorjs.plugins.geolocation;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
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

    @SuppressWarnings("MissingPermission")
    public void sendLocation(boolean enableHighAccuracy, final LocationResultCallback resultCallback) {
        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
        if (resultCode == ConnectionResult.SUCCESS) {
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            if (this.isLocationServicesEnabled()) {
                boolean networkEnabled = false;

                try {
                    networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                } catch (Exception ex) {}

                int lowPriority = networkEnabled ? Priority.PRIORITY_BALANCED_POWER_ACCURACY : Priority.PRIORITY_LOW_POWER;
                int priority = enableHighAccuracy ? Priority.PRIORITY_HIGH_ACCURACY : lowPriority;

                LocationServices
                    .getFusedLocationProviderClient(context)
                    .getCurrentLocation(priority, null)
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
                resultCallback.error("location disabled");
            }
        } else {
            resultCallback.error("Google Play Services not available");
        }
    }

    @SuppressWarnings("MissingPermission")
    public void requestLocationUpdates(boolean enableHighAccuracy, int timeout, final LocationResultCallback resultCallback) {
        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
        if (resultCode == ConnectionResult.SUCCESS) {
            clearLocationUpdates();
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (this.isLocationServicesEnabled()) {
                boolean networkEnabled = false;

                try {
                    networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                } catch (Exception ex) {}

                int lowPriority = networkEnabled ? Priority.PRIORITY_BALANCED_POWER_ACCURACY : Priority.PRIORITY_LOW_POWER;
                int priority = enableHighAccuracy ? Priority.PRIORITY_HIGH_ACCURACY : lowPriority;

                LocationRequest locationRequest = new LocationRequest.Builder(10000)
                    .setMaxUpdateDelayMillis(timeout)
                    .setMinUpdateIntervalMillis(5000)
                    .setPriority(priority)
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
                resultCallback.error("location disabled");
            }
        } else {
            resultCallback.error("Google Play Services not available");
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
