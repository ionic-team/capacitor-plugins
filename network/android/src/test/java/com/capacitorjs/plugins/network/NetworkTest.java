package com.capacitorjs.plugins.network;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.net.ConnectivityManager;
import org.junit.Test;

public class NetworkTest {

    @Test
    public void isConstrainedReturnsTrueWhenMeteredBackgroundDataIsRestricted() {
        assertTrue(Network.isConstrained(ConnectivityManager.RESTRICT_BACKGROUND_STATUS_ENABLED, true, false));
    }

    @Test
    public void isConstrainedReturnsFalseWhenUnmeteredBackgroundDataIsRestricted() {
        assertFalse(Network.isConstrained(ConnectivityManager.RESTRICT_BACKGROUND_STATUS_ENABLED, false, false));
    }

    @Test
    public void isConstrainedReturnsTrueWhenBandwidthIsConstrained() {
        assertTrue(Network.isConstrained(ConnectivityManager.RESTRICT_BACKGROUND_STATUS_DISABLED, false, true));
    }

    @Test
    public void isConstrainedReturnsFalseWhenBackgroundDataIsUnrestricted() {
        assertFalse(Network.isConstrained(ConnectivityManager.RESTRICT_BACKGROUND_STATUS_DISABLED, true, false));
        assertFalse(Network.isConstrained(ConnectivityManager.RESTRICT_BACKGROUND_STATUS_WHITELISTED, true, false));
    }
}
