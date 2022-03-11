package com.capacitorjs.plugins.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.ConnectivityManager.NetworkCallback;
import android.net.NetworkCapabilities;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import com.getcapacitor.JSObject;

@RequiresApi(api = Build.VERSION_CODES.N)
public class Network {

    /**
     * Interface for callbacks when network status changes.
     */
    interface NetworkStatusChangeListener {
        void onNetworkStatusChanged();
    }

    class ConnectivityCallback extends NetworkCallback {

        @Override
        public void onLost(@NonNull android.net.Network network) {
            super.onLost(network);
            statusChangeListener.onNetworkStatusChanged();
        }

        @Override
        public void onCapabilitiesChanged(@NonNull android.net.Network network, @NonNull NetworkCapabilities networkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities);
            statusChangeListener.onNetworkStatusChanged();
        }
    }

    @Nullable
    private NetworkStatusChangeListener statusChangeListener;

    private ConnectivityCallback connectivityCallback = new ConnectivityCallback();
    private Context context;
    private ConnectivityManager connectivityManager;

    /**
     * Create network monitoring object.
     * @param context
     */
    public Network(@NonNull Context context) {
        this.context = context;
        this.connectivityManager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
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
     * @return JSObject
     */
    public JSObject getNetworkStatus() {
        boolean connected = false;
        String connectionType = "none";
        if (this.connectivityManager != null) {
            NetworkCapabilities capabilities = this.connectivityManager.getNetworkCapabilities(this.connectivityManager.getActiveNetwork());
            if (this.connectivityManager.getActiveNetwork() != null && capabilities != null) {
                connected = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    connectionType = "wifi";
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    connectionType = "cellular";
                } else {
                    connectionType = "unknown";
                }
            }
        }
        return (new JSObject()).put("connected", connected).put("connectionType", connectionType);
    }

    /**
     * Register a network callback.
     */
    public void startMonitoring() {
        connectivityManager.registerDefaultNetworkCallback(connectivityCallback);
    }

    /**
     * Unregister the network callback.
     */
    public void stopMonitoring() {
        connectivityManager.unregisterNetworkCallback(connectivityCallback);
    }
}
