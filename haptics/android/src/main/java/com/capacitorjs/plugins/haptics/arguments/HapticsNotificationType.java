package com.capacitorjs.plugins.haptics.arguments;

public enum HapticsNotificationType implements HapticsVibrationType {
    SUCCESS("SUCCESS", new long[] { 0, 35, 65, 21 }, new int[] { 0, 250, 0, 180 }, new long[] { 0, 35, 65, 21 }),
    WARNING(
        "WARNING",
        new long[] { 0, 30, 40, 30, 50, 60 },
        new int[] { 255, 255, 255, 255, 255, 255 },
        new long[] { 0, 30, 40, 30, 50, 60 }
    ),
    ERROR("ERROR", new long[] { 0, 27, 45, 50 }, new int[] { 0, 120, 0, 250 }, new long[] { 0, 27, 45, 50 });

    private final String mType;
    private final long[] mTimings;
    private final int[] mAmplitudes;
    private final long[] mOldSDKPattern;

    HapticsNotificationType(String type, long[] timings, int[] amplitudes, long[] oldSDKPattern) {
        mType = type;
        mTimings = timings;
        mAmplitudes = amplitudes;
        mOldSDKPattern = oldSDKPattern;
    }

    public static HapticsNotificationType fromString(String type) {
        for (HapticsNotificationType nt : values()) {
            if (nt.mType.equals(type)) {
                return nt;
            }
        }
        return SUCCESS;
    }

    @Override
    public long[] getTimings() {
        return mTimings;
    }

    @Override
    public int[] getAmplitudes() {
        return mAmplitudes;
    }

    @Override
    public long[] getOldSDKPattern() {
        return mOldSDKPattern;
    }
}
