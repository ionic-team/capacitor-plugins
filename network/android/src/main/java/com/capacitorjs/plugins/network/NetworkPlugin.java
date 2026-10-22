package com.capacitorjs.plugins.network;

import android.util.Log;
import androidx.annotation.Nullable;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "Network")
public class NetworkPlugin extends Plugin {

    private Network implementation;
    public static final String NETWORK_CHANGE_EVENT = "networkStatusChange";

    @Nullable
    private NetworkStatus lastNotifiedStatus = null;

    /**
     * Monitor for network status changes and fire our event.
     */
    @Override
    public void load() {
        implementation = new Network(getContext());
        implementation.setStatusChangeListener(this::notifyIfChanged);
        implementation.startMonitoring();
    }

    /**
     * Clean up callback to prevent leaks.
     */
    @Override
    protected void handleOnDestroy() {
        implementation.setStatusChangeListener(null);
        implementation.stopMonitoring();
    }

    /**
     * Get current network status information.
     * @param call
     */
    @PluginMethod
    public void getStatus(PluginCall call) {
        call.resolve(parseNetworkStatus(implementation.getNetworkStatus()));
    }

    /**
     * Register the network callback on resume
     */
    @Override
    protected void handleOnResume() {
        implementation.startMonitoring();
        // Registering delivers the current default network, but nothing is delivered when there is
        // none, so a connection lost while paused has to be reported from here.
        NetworkStatus afterPauseNetworkStatus = implementation.getNetworkStatus();
        if (!afterPauseNetworkStatus.connected && hasNotified()) {
            Log.d("Capacitor/NetworkPlugin", "Network status is not connected after resume. Notifying listeners if it changed.");
            notifyIfChanged(afterPauseNetworkStatus);
        }
    }

    /**
     * Unregister the network callback on pause to avoid leaking it
     */
    @Override
    protected void handleOnPause() {
        implementation.stopMonitoring();
    }

    private synchronized boolean hasNotified() {
        return lastNotifiedStatus != null;
    }

    private synchronized void notifyIfChanged(NetworkStatus status) {
        if (status.equals(lastNotifiedStatus)) {
            return;
        }
        lastNotifiedStatus = status;
        notifyListeners(NETWORK_CHANGE_EVENT, parseNetworkStatus(status));
    }

    private JSObject parseNetworkStatus(NetworkStatus networkStatus) {
        JSObject jsObject = new JSObject();
        jsObject.put("connected", networkStatus.connected);
        jsObject.put("connectionType", networkStatus.connectionType.getConnectionType());
        return jsObject;
    }
}
