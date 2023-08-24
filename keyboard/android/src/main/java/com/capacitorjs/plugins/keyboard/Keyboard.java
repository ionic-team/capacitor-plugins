package com.capacitorjs.plugins.keyboard;

import android.content.Context;
import android.graphics.Insets;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowInsets;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.getcapacitor.Logger;

public class Keyboard {

    interface KeyboardEventListener {
        void onKeyboardEvent(String event, int size);
    }

    private AppCompatActivity activity;
    private ViewTreeObserver.OnGlobalLayoutListener list;
    private View rootView;
    private View mChildOfContent;
    private int usableHeightPrevious;
    private FrameLayout.LayoutParams frameLayoutParams;

    @Nullable
    public KeyboardEventListener getKeyboardEventListener() {
        return keyboardEventListener;
    }

    public void setKeyboardEventListener(@Nullable KeyboardEventListener keyboardEventListener) {
        this.keyboardEventListener = keyboardEventListener;
    }

    @Nullable
    private KeyboardEventListener keyboardEventListener;

    static final String EVENT_KB_WILL_SHOW = "keyboardWillShow";
    static final String EVENT_KB_DID_SHOW = "keyboardDidShow";
    static final String EVENT_KB_WILL_HIDE = "keyboardWillHide";
    static final String EVENT_KB_DID_HIDE = "keyboardDidHide";

    public Keyboard(AppCompatActivity activity, boolean resizeOnFullScreen) {
        this.activity = activity;
        //calculate density-independent pixels (dp)
        //http://developer.android.com/guide/practices/screens_support.html
        DisplayMetrics dm = activity.getResources().getDisplayMetrics();
        final float density = dm.density;

        //http://stackoverflow.com/a/4737265/1091751 detect if keyboard is showing
        FrameLayout content = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        rootView = content.getRootView();
        list =
            new ViewTreeObserver.OnGlobalLayoutListener() {
                int previousHeightDiff = 0;

                @Override
                public void onGlobalLayout() {
                    Rect r = new Rect();
                    //r will be populated with the coordinates of your view that area still visible.
                    rootView.getWindowVisibleDisplayFrame(r);

                    // cache properties for later use
                    int rootViewHeight = rootView.getRootView().getHeight();
                    int resultBottom = r.bottom;
                    int screenHeight;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        Insets windowInsets = rootView.getRootWindowInsets().getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
                        screenHeight = rootViewHeight;
                        resultBottom = resultBottom + windowInsets.bottom;
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        WindowInsets windowInsets = rootView.getRootWindowInsets();
                        int stableInsetBottom = getLegacyStableInsetBottom(windowInsets);
                        screenHeight = rootViewHeight;
                        resultBottom = resultBottom + stableInsetBottom;
                    } else {
                        Point size = getLegacySizePoint();
                        screenHeight = size.y;
                    }

                    int heightDiff = screenHeight - resultBottom;

                    int pixelHeightDiff = (int) (heightDiff / density);

                    if (pixelHeightDiff > 100 && pixelHeightDiff != previousHeightDiff) { // if more than 100 pixels, its probably a keyboard...
                        if (resizeOnFullScreen) {
                            possiblyResizeChildOfContent(true);
                        }

                        if (keyboardEventListener != null) {
                            keyboardEventListener.onKeyboardEvent(EVENT_KB_WILL_SHOW, pixelHeightDiff);
                            keyboardEventListener.onKeyboardEvent(EVENT_KB_DID_SHOW, pixelHeightDiff);
                        } else {
                            Logger.warn("Native Keyboard Event Listener not found");
                        }
                    } else if (pixelHeightDiff != previousHeightDiff && (previousHeightDiff - pixelHeightDiff) > 100) {
                        if (resizeOnFullScreen) {
                            possiblyResizeChildOfContent(false);
                        }

                        if (keyboardEventListener != null) {
                            keyboardEventListener.onKeyboardEvent(EVENT_KB_WILL_HIDE, 0);
                            keyboardEventListener.onKeyboardEvent(EVENT_KB_DID_HIDE, 0);
                        } else {
                            Logger.warn("Native Keyboard Event Listener not found");
                        }
                    }
                    previousHeightDiff = pixelHeightDiff;
                }

                private void possiblyResizeChildOfContent(boolean keyboardShown) {
                    int usableHeightNow = keyboardShown ? computeUsableHeight() : -1;
                    if (usableHeightPrevious != usableHeightNow) {
                        frameLayoutParams.height = usableHeightNow;
                        mChildOfContent.requestLayout();
                        usableHeightPrevious = usableHeightNow;
                    }
                }

                private int computeUsableHeight() {
                    Rect r = new Rect();
                    mChildOfContent.getWindowVisibleDisplayFrame(r);
                    return isOverlays() ? r.bottom : r.height();
                }

                @SuppressWarnings("deprecation")
                private boolean isOverlays() {
                    final Window window = activity.getWindow();
                    return (
                        (window.getDecorView().getSystemUiVisibility() & View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN) ==
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    );
                }
            };
        mChildOfContent = content.getChildAt(0);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(list);
        frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();
    }

    @SuppressWarnings("deprecation")
    private int getLegacyStableInsetBottom(WindowInsets windowInsets) {
        return windowInsets.getStableInsetBottom();
    }

    @SuppressWarnings("deprecation")
    private Point getLegacySizePoint() {
        // calculate screen height differently for android versions <23: Lollipop 5.x, Marshmallow 6.x
        //http://stackoverflow.com/a/29257533/3642890 beware of nexus 5
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public void show() {
        ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(activity.getCurrentFocus(), 0);
    }

    public boolean hide() {
        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View v = activity.getCurrentFocus();
        if (v == null) {
            return false;
        } else {
            inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            return true;
        }
    }
}
