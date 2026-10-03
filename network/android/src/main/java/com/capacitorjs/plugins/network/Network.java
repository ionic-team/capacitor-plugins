package com.capacitorjs.plugins.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.ConnectivityManager.NetworkCallback;
import android.net.NetworkCapabilities;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Network {

    /**
     * Interface for callbacks when network status changes.
     */
    interface NetworkStatusChangeListener {
        void onNetworkStatusChanged(boolean wasLostEvent);
    }

    class ConnectivityCallback extends NetworkCallback {

        @Override
        public void onLost(@NonNull android.net.Network network) {
            super.onLost(network);
            statusChangeListener.onNetworkStatusChanged(true);
        }

        @Override
        public void onCapabilitiesChanged(@NonNull android.net.Network network, @NonNull NetworkCapabilities networkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities);
            statusChangeListener.onNetworkStatusChanged(false);
        }
    }

    @Nullable
    private NetworkStatusChangeListener statusChangeListener;

    private ConnectivityCallback connectivityCallback;
    private Context context;
    private ConnectivityManager connectivityManager;
    private BroadcastReceiver restrictBackgroundReceiver;

    /**
     * Create network monitoring object.
     * @param context
     */
    public Network(@NonNull Context context) {
        this.context = context;
        this.connectivityManager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.connectivityCallback = new ConnectivityCallback();
        this.restrictBackgroundReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (ConnectivityManager.ACTION_RESTRICT_BACKGROUND_CHANGED.equals(intent.getAction()) && statusChangeListener != null) {
                    statusChangeListener.onNetworkStatusChanged(false);
                }
            }
        };
    }

    /**
     * Set the object to receive callbacks.
     * @param listener
     */
    public void setStatusChangeListener(@Nullable NetworkStatusChangeListener listener) {
        this.statusChangeListener = listener;
    }

    /**
     * Return the object that is receiving the callbacks.
     * @return
     */
    @Nullable
    public NetworkStatusChangeListener getStatusChangeListener() {
        return statusChangeListener;
    }

    /**
     * Get the current network information.
     * @return NetworkStatus
     */
    public NetworkStatus getNetworkStatus() {
        NetworkStatus networkStatus = new NetworkStatus();
        if (this.connectivityManager != null) {
            android.net.Network activeNetwork = this.connectivityManager.getActiveNetwork();
            NetworkCapabilities capabilities = this.connectivityManager.getNetworkCapabilities(activeNetwork);
            if (activeNetwork != null && capabilities != null) {
                networkStatus.connected =
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) &&
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
                networkStatus.expensive = !capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED);
                networkStatus.constrained = isConstrained(
                    connectivityManager.getRestrictBackgroundStatus(),
                    networkStatus.expensive,
                    isBandwidthConstrained(capabilities)
                );
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    networkStatus.connectionType = NetworkStatus.ConnectionType.WIFI;
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    networkStatus.connectionType = NetworkStatus.ConnectionType.CELLULAR;
                } else {
                    networkStatus.connectionType = NetworkStatus.ConnectionType.UNKNOWN;
                }
            }
        }
        return networkStatus;
    }

    static boolean isConstrained(int restrictBackgroundStatus, boolean expensive, boolean bandwidthConstrained) {
        return (expensive && restrictBackgroundStatus == ConnectivityManager.RESTRICT_BACKGROUND_STATUS_ENABLED) || bandwidthConstrained;
    }

    private static boolean isBandwidthConstrained(NetworkCapabilities capabilities) {
        return (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM &&
            !capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_BANDWIDTH_CONSTRAINED)
        );
    }

    @SuppressWarnings("deprecation")
    private NetworkStatus getAndParseNetworkInfo() {
        NetworkStatus networkStatus = new NetworkStatus();
        android.net.NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            networkStatus.connected = networkInfo.isConnected();
            String typeName = networkInfo.getTypeName();
            if (typeName.equals("WIFI")) {
                networkStatus.connectionType = NetworkStatus.ConnectionType.WIFI;
            } else if (typeName.equals("MOBILE")) {
                networkStatus.connectionType = NetworkStatus.ConnectionType.CELLULAR;
            }
        }
        return networkStatus;
    }

    /**
     * Register a network callback.
     */
    public void startMonitoring() {
        connectivityManager.registerDefaultNetworkCallback(connectivityCallback);
        IntentFilter filter = new IntentFilter(ConnectivityManager.ACTION_RESTRICT_BACKGROUND_CHANGED);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(restrictBackgroundReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            context.registerReceiver(restrictBackgroundReceiver, filter);
        }
    }

    /**
     * Unregister the network callback.
     */
    public void stopMonitoring() {
        connectivityManager.unregisterNetworkCallback(connectivityCallback);
        context.unregisterReceiver(restrictBackgroundReceiver);
    }
}
