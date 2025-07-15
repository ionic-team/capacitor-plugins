package com.capacitorjs.plugins.keyboard;

import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsAnimationCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.List;

public class Keyboard {

    interface KeyboardEventListener {
        void onKeyboardEvent(String event, int size);
    }

    private AppCompatActivity activity;
    private View rootView;
    private int usableHeightPrevious;
    private FrameLayout.LayoutParams frameLayoutParams;
    private View mChildOfContent;

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

        //http://stackoverflow.com/a/4737265/1091751 detect if keyboard is showing
        FrameLayout content = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        rootView = content.getRootView();

        ViewCompat.setWindowInsetsAnimationCallback(
            rootView,
            new WindowInsetsAnimationCompat.Callback(WindowInsetsAnimationCompat.Callback.DISPATCH_MODE_STOP) {
                @NonNull
                @Override
                public WindowInsetsCompat onProgress(
                    @NonNull WindowInsetsCompat insets,
                    @NonNull List<WindowInsetsAnimationCompat> runningAnimations
                ) {
                    return insets;
                }

                @NonNull
                @Override
                public WindowInsetsAnimationCompat.BoundsCompat onStart(
                    @NonNull WindowInsetsAnimationCompat animation,
                    @NonNull WindowInsetsAnimationCompat.BoundsCompat bounds
                ) {
                    boolean showingKeyboard = ViewCompat.getRootWindowInsets(rootView).isVisible(WindowInsetsCompat.Type.ime());
                    WindowInsetsCompat insets = ViewCompat.getRootWindowInsets(rootView);
                    int imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom;
                    DisplayMetrics dm = activity.getResources().getDisplayMetrics();
                    final float density = dm.density;

                    if (resizeOnFullScreen) {
                        possiblyResizeChildOfContent(showingKeyboard);
                    }

                    if (showingKeyboard) {
                        keyboardEventListener.onKeyboardEvent(EVENT_KB_WILL_SHOW, Math.round(imeHeight / density));
                    } else {
                        keyboardEventListener.onKeyboardEvent(EVENT_KB_WILL_HIDE, 0);
                    }
                    return super.onStart(animation, bounds);
                }

                @Override
                public void onEnd(@NonNull WindowInsetsAnimationCompat animation) {
                    super.onEnd(animation);
                    boolean showingKeyboard = ViewCompat.getRootWindowInsets(rootView).isVisible(WindowInsetsCompat.Type.ime());
                    WindowInsetsCompat insets = ViewCompat.getRootWindowInsets(rootView);
                    int imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom;
                    DisplayMetrics dm = activity.getResources().getDisplayMetrics();
                    final float density = dm.density;

                    if (showingKeyboard) {
                        keyboardEventListener.onKeyboardEvent(EVENT_KB_DID_SHOW, Math.round(imeHeight / density));
                    } else {
                        keyboardEventListener.onKeyboardEvent(EVENT_KB_DID_HIDE, 0);
                    }
                }
            }
        );

        mChildOfContent = content.getChildAt(0);
        frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();
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
            (window.getDecorView().getSystemUiVisibility() & View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN) == View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
    }
}
