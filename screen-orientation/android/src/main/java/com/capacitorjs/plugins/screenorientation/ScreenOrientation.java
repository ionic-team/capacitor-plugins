package com.capacitorjs.plugins.screenorientation;

import android.content.pm.ActivityInfo;
import android.view.Surface;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ScreenOrientation {

    private AppCompatActivity activity;

    @Nullable
    private int configOrientation;

    public ScreenOrientation(AppCompatActivity activity) {
        this.activity = activity;
    }

    public String getCurrentOrientationType() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            return fromRotationToOrientationType(activity.getDisplay().getRotation());
        } else {
            return fromRotationToOrientationType(getLegacyDisplayRotation());
        }
    }

    public void lock(String orientationType) {
        int orientationEnum = fromOrientationTypeToEnum(orientationType);
        activity.setRequestedOrientation(orientationEnum);
    }

    public void unlock() {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    public boolean hasOrientationChanged(int orientation) {
        if (orientation == configOrientation) {
            return false;
        } else {
            this.configOrientation = orientation;
            return true;
        }
    }

    private String fromRotationToOrientationType(int rotation) {
        switch (rotation) {
            case Surface.ROTATION_90:
                return "landscape-primary";
            case Surface.ROTATION_180:
                return "portrait-secondary";
            case Surface.ROTATION_270:
                return "landscape-secondary";
            default:
                return "portrait-primary";
        }
    }

    private int fromOrientationTypeToEnum(String orientationType) {
        switch (orientationType) {
            case "any":
                return ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
            case "landscape":
            case "landscape-primary":
                return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            case "landscape-secondary":
                return ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
            case "portrait-secondary":
                return ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
            default:
                // Case: portrait-primary
                return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        }
    }

    @SuppressWarnings("deprecation")
    private int getLegacyDisplayRotation() {
        return activity.getWindowManager().getDefaultDisplay().getRotation();
    }
}
