package com.capacitorjs.plugins.network;

public class NetworkStatus {

    public enum ConnectionType {
        WIFI,
        CELLULAR,
        NONE,
        UNKNOWN
    }

    public boolean connected = false;
    public ConnectionType connectionType = ConnectionType.NONE;
}
