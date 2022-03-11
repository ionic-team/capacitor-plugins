package com.capacitorjs.plugins.network;

import android.os.Build;
import androidx.annotation.RequiresApi;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@RequiresApi(api = Build.VERSION_CODES.N)
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
        implementation.setStatusChangeListener(this::updateNetworkStatus);
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
        implementation.startMonitoring();
    }

    /**
     * Unregister the IntentReceiver on pause to avoid leaking it
     */
    @Override
    protected void handleOnPause() {
        implementation.stopMonitoring();
    }

    private void updateNetworkStatus() {
        notifyListeners(NETWORK_CHANGE_EVENT, parseNetworkStatus(implementation.getNetworkStatus()));
    }

    private JSObject parseNetworkStatus(NetworkStatus networkStatus) {
        JSObject jsObject = new JSObject();
        jsObject.put("connected", networkStatus.connected);
        String connectionType = "none";
        switch (networkStatus.connectionType) {
            case WIFI:
                connectionType = "wifi";
                break;
            case UNKNOWN:
                connectionType = "unknown";
                break;
            case CELLULAR:
                connectionType = "cellular";
                break;
        }
        jsObject.put("connectionType", connectionType);
        return jsObject;
    }
}
