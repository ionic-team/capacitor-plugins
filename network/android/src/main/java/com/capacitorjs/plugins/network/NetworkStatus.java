package com.capacitorjs.plugins.network;

import java.util.Objects;

public class NetworkStatus {

    public enum ConnectionType {
        WIFI("wifi"),
        CELLULAR("cellular"),
        NONE("none"),
        UNKNOWN("unknown");

        private String connectionType;

        ConnectionType(String connectionType) {
            this.connectionType = connectionType;
        }

        public String getConnectionType() {
            return this.connectionType;
        }
    }

    public boolean connected = false;
    public ConnectionType connectionType = ConnectionType.NONE;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NetworkStatus)) {
            return false;
        }
        NetworkStatus other = (NetworkStatus) o;
        return connected == other.connected && connectionType == other.connectionType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(connected, connectionType);
    }
}
