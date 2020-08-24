package com.capacitorjs.plugins.haptics.arguments;

public interface HapticsVibrationType {
    long[] getTimings();

    int[] getAmplitudes();

    long[] getOldSDKPattern();
}
