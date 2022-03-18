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
        Network.NetworkStatusChangeListener listener = new Network.NetworkStatusChangeListener() {
            @Override
            public void onNetworkStatusChanged(boolean wasLostEvent) {
                if (wasLostEvent) {
                    JSObject jsObject = new JSObject();
                    jsObject.put("connected", false);
                    jsObject.put("connectionType", "none");
                    notifyListeners(NETWORK_CHANGE_EVENT, jsObject);
                } else {
                    updateNetworkStatus();
                }
            }

            @Override
            public void onLegacyNetworkStatusChanged() {
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
        JSObject x = parseNetworkStatus(implementation.getNetworkStatus());
        call.resolve(x);
    }

    /**
     * Register the IntentReceiver on resume
     */
    @Override
    @SuppressWarnings("deprecation")
    protected void handleOnResume() {
        if (Build.VERSION.SDK_INT >= 24) {
            implementation.startMonitoring();
        } else {
            implementation.startMonitoring(getActivity());
        }
    }

    /**
     * Unregister the IntentReceiver on pause to avoid leaking it
     */
    @Override
    @SuppressWarnings("deprecation")
    protected void handleOnPause() {
        if (Build.VERSION.SDK_INT >= 24) {
            implementation.stopMonitoring();
        } else {
            implementation.stopMonitoring(getActivity());
        }
    }

    private void updateNetworkStatus() {
        JSObject x = parseNetworkStatus(implementation.getNetworkStatus());
        notifyListeners(NETWORK_CHANGE_EVENT, x);
    }

    private JSObject parseNetworkStatus(NetworkStatus networkStatus) {
        JSObject jsObject = new JSObject();
        jsObject.put("connected", networkStatus.connected);
        jsObject.put("connectionType", networkStatus.connectionType.getConnectionType());
        return jsObject;
    }
}
