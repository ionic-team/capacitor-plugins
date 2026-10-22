package com.capacitorjs.plugins.network;

import android.content.BroadcastReceiver;
import android.content.Context;
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
        void onNetworkStatusChanged(NetworkStatus status);
    }

    // Status is derived from the callback arguments. Querying ConnectivityManager from inside a
    // NetworkCallback can return state that is already out of sync with the callback.
    class ConnectivityCallback extends NetworkCallback {

        @Nullable
        private android.net.Network currentNetwork;

        @Override
        public void onAvailable(@NonNull android.net.Network network) {
            super.onAvailable(network);
            currentNetwork = network;
            // From API 26 onCapabilitiesChanged always follows onAvailable for the default network.
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                notifyStatus(statusFromCapabilities(connectivityManager.getNetworkCapabilities(network)));
            }
        }

        @Override
        public void onLost(@NonNull android.net.Network network) {
            super.onLost(network);
            // A late onLost for the previous default network must not override the new one.
            if (currentNetwork != null && !currentNetwork.equals(network)) {
                return;
            }
            currentNetwork = null;
            notifyStatus(new NetworkStatus());
        }

        @Override
        public void onCapabilitiesChanged(@NonNull android.net.Network network, @NonNull NetworkCapabilities networkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities);
            currentNetwork = network;
            notifyStatus(statusFromCapabilities(networkCapabilities));
        }

        private void notifyStatus(NetworkStatus status) {
            NetworkStatusChangeListener listener = statusChangeListener;
            if (listener != null) {
                listener.onNetworkStatusChanged(status);
            }
        }
    }

    @Nullable
    private volatile NetworkStatusChangeListener statusChangeListener;

    private ConnectivityCallback connectivityCallback;
    private boolean monitoring = false;
    private Context context;
    private ConnectivityManager connectivityManager;
    private BroadcastReceiver receiver;

    /**
     * Create network monitoring object.
     * @param context
     */
    public Network(@NonNull Context context) {
        this.context = context;
        this.connectivityManager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.connectivityCallback = new ConnectivityCallback();
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
            NetworkCapabilities capabilities = this.connectivityManager.getNetworkCapabilities(this.connectivityManager.getActiveNetwork());
            if (activeNetwork != null && capabilities != null) {
                networkStatus = statusFromCapabilities(capabilities);
            }
        }
        return networkStatus;
    }

    static NetworkStatus statusFromCapabilities(@Nullable NetworkCapabilities capabilities) {
        NetworkStatus networkStatus = new NetworkStatus();
        if (capabilities != null) {
            networkStatus.connected =
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                networkStatus.connectionType = NetworkStatus.ConnectionType.WIFI;
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                networkStatus.connectionType = NetworkStatus.ConnectionType.CELLULAR;
            } else {
                networkStatus.connectionType = NetworkStatus.ConnectionType.UNKNOWN;
            }
        }
        return networkStatus;
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
     * Register a network callback. Does nothing if it is already registered.
     */
    public synchronized void startMonitoring() {
        if (monitoring) {
            return;
        }
        connectivityManager.registerDefaultNetworkCallback(connectivityCallback);
        monitoring = true;
    }

    /**
     * Unregister the network callback. Does nothing if it is not registered.
     */
    public synchronized void stopMonitoring() {
        if (!monitoring) {
            return;
        }
        monitoring = false;
        connectivityManager.unregisterNetworkCallback(connectivityCallback);
    }
}
