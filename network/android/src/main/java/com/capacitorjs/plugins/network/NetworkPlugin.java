package com.capacitorjs.plugins.network;

import android.Manifest;
import android.net.NetworkInfo;
import com.getcapacitor.JSObject;
import com.getcapacitor.Logger;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;

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
        call.resolve(getStatusJSObject(implementation.getNetworkStatus()));
    }

    /**
     * Register the IntentReceiver on resume
     */
    @Override
    protected void handleOnResume() {
        implementation.startMonitoring(getActivity());
    }

    /**
     * Unregister the IntentReceiver on pause to avoid leaking it
     */
    @Override
    protected void handleOnPause() {
        implementation.stopMonitoring(getActivity());
    }

    private void updateNetworkStatus() {
        notifyListeners(NETWORK_CHANGE_EVENT, getStatusJSObject(implementation.getNetworkStatus()));
    }

    /**
     * Transform a NetworkInfo object into our JSObject for returning to client
     * @param info
     * @return
     */
    private JSObject getStatusJSObject(NetworkInfo info) {
        JSObject ret = new JSObject();
        if (info == null) {
            ret.put("connected", false);
            ret.put("connectionType", "none");
        } else {
            ret.put("connected", info.isConnected());
            ret.put("connectionType", getNormalizedTypeName(info));
        }
        return ret;
    }

    /**
     * Convert the Android-specific naming for network types into our cross-platform type
     * @param info
     * @return
     */
    private String getNormalizedTypeName(NetworkInfo info) {
        String typeName = info.getTypeName();
        if (typeName.equals("WIFI")) {
            return "wifi";
        }
        if (typeName.equals("MOBILE")) {
            return "cellular";
        }
        return "none";
    }
}
