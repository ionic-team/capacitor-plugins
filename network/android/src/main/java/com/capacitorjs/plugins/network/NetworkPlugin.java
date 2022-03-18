package com.capacitorjs.plugins.network;

import android.os.Build;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "Network")
public class NetworkPlugin extends Plugin {

    private Network implementation;
    public static final String NETWORK_CHANGE_EVENT = "networkStatusChange";

    /**
     * Monitor for network status changes and fire our event.
     */
    @Override
    public void load() {
        implementation = new Network(getContext());
        Network.NetworkStatusChangeListener listener = wasLostEvent -> {
            if (wasLostEvent) {
                JSObject jsObject = new JSObject();
                jsObject.put("connected", false);
                jsObject.put("connectionType", "none");
                notifyListeners(NETWORK_CHANGE_EVENT, jsObject);
            } else {
                updateNetworkStatus();
            }
        };
        implementation.setStatusChangeListener(listener);
    }

    /**
     * Clean up callback to prevent leaks.
     */
    @Override
    protected void handleOnDestroy() {
        implementation.setStatusChangeListener(null);
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
     * Register the IntentReceiver on resume
     */
    @Override
    protected void handleOnResume() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            implementation.startMonitoring();
        } else {
            implementation.startMonitoring(getActivity());
        }
    }

    /**
     * Unregister the IntentReceiver on pause to avoid leaking it
     */
    @Override
    protected void handleOnPause() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            implementation.stopMonitoring();
        } else {
            implementation.stopMonitoring(getActivity());
        }
    }

    private void updateNetworkStatus() {
        notifyListeners(NETWORK_CHANGE_EVENT, parseNetworkStatus(implementation.getNetworkStatus()));
    }

    private JSObject parseNetworkStatus(NetworkStatus networkStatus) {
        JSObject jsObject = new JSObject();
        jsObject.put("connected", networkStatus.connected);
        jsObject.put("connectionType", networkStatus.connectionType.getConnectionType());
        return jsObject;
    }
}
