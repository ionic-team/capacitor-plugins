package com.capacitorjs.plugins.network;

import android.Manifest;
import android.net.NetworkInfo;
import com.getcapacitor.JSObject;
import com.getcapacitor.Logger;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

@NativePlugin(name = "Network", permissions = { Manifest.permission.ACCESS_NETWORK_STATE })
public class NetworkPlugin extends Plugin {
    private Network implementation;
    public static final String NETWORK_CHANGE_EVENT = "networkStatusChange";
    private static final String PERMISSION_NOT_SET = Manifest.permission.ACCESS_NETWORK_STATE + " not set in AndroidManifest.xml";

    /**
     * Monitor for network status changes and fire our event.
     */
    @Override
    @SuppressWarnings("MissingPermission")
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
    @SuppressWarnings("MissingPermission")
    @PluginMethod
    public void getStatus(PluginCall call) {
        if (hasRequiredPermissions()) {
            call.resolve(getStatusJSObject(implementation.getNetworkStatus()));
        } else {
            call.reject(PERMISSION_NOT_SET);
        }
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

    @SuppressWarnings("MissingPermission")
    private void updateNetworkStatus() {
        if (hasRequiredPermissions()) {
            notifyListeners(NETWORK_CHANGE_EVENT, getStatusJSObject(implementation.getNetworkStatus()));
        } else {
            Logger.error(getLogTag(), PERMISSION_NOT_SET, null);
        }
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
