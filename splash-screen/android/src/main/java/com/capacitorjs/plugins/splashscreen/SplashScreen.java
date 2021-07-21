package com.capacitorjs.plugins.splashscreen;

import android.animation.Animator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.view.*;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import com.getcapacitor.Logger;

/**
 * A Splash Screen service for showing and hiding a splash screen in the app.
 */
public class SplashScreen {

    private Dialog dialog;
    private View splashImage;
    private ProgressBar spinnerBar;
    private WindowManager windowManager;
    private boolean isVisible = false;
    private boolean isHiding = false;
    private Context context;
    private SplashScreenConfig config;

    SplashScreen(Context context, SplashScreenConfig config) {
        this.context = context;
        this.config = config;
    }

    /**
     * Show the splash screen on launch without fading in
     *
     * @param activity
     */
    public void showOnLaunch(final AppCompatActivity activity) {
        if (config.getLaunchShowDuration() == 0) {
            return;
        }
        SplashScreenSettings settings = new SplashScreenSettings();
        settings.setShowDuration(config.getLaunchShowDuration());
        settings.setAutoHide(config.isLaunchAutoHide());
        settings.setFadeInDuration(config.getLaunchFadeInDuration());
        if (config.isUsingDialog()) {
            showDialog(activity, settings, null, true);
        } else {
            show(activity, settings, null, true);
        }
    }

    /**
     * Show the Splash Screen
     *
     * @param activity
     * @param settings Settings used to show the Splash Screen
     * @param splashListener A listener to handle the finish of the animation (if any)
     */
    public void show(final AppCompatActivity activity, final SplashScreenSettings settings, final SplashListener splashListener) {
        if (config.isUsingDialog()) {
            showDialog(activity, settings, splashListener, false);
        } else {
            show(activity, settings, splashListener, false);
        }
    }

    private void showDialog(
        final AppCompatActivity activity,
        final SplashScreenSettings settings,
        final SplashListener splashListener,
        final boolean isLaunchSplash
    ) {
        if (activity == null || activity.isFinishing()) return;

        if (isVisible) {
            splashListener.completed();
            return;
        }

        activity.runOnUiThread(
            () -> {
                if (config.isImmersive()) {
                    dialog = new Dialog(activity, R.style.capacitor_immersive_style);
                } else if (config.isFullScreen()) {
                    dialog = new Dialog(activity, R.style.capacitor_full_screen_style);
                } else {
                    dialog = new Dialog(activity, R.style.capacitor_default_style);
                }
                int splashId = 0;
                if (config.getLayoutName() != null) {
                    splashId = context.getResources().getIdentifier(config.getLayoutName(), "layout", context.getPackageName());
                    if (splashId == 0) {
                        Logger.warn("Layout not found, using default");
                    }
                }
                if (splashId != 0) {
                    dialog.setContentView(splashId);
                } else {
                    Drawable splash = getSplashDrawable();
                    LinearLayout parent = new LinearLayout(context);
                    parent.setLayoutParams(
                        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    );
                    parent.setOrientation(LinearLayout.VERTICAL);
                    if (splash != null) {
                        parent.setBackground(splash);
                    }
                    dialog.setContentView(parent);
                }

                dialog.setCancelable(false);
                if (!dialog.isShowing()) {
                    dialog.show();
                }
                isVisible = true;

                if (settings.isAutoHide()) {
                    new Handler()
                        .postDelayed(
                            () -> {
                                hideDialog(activity, isLaunchSplash);

                                if (splashListener != null) {
                                    splashListener.completed();
                                }
                            },
                            settings.getShowDuration()
                        );
                } else {
                    // If no autoHide, call complete
                    if (splashListener != null) {
                        splashListener.completed();
                    }
                }
            }
        );
    }

    /**
     * Hide the Splash Screen
     *
     * @param settings Settings used to hide the Splash Screen
     */
    public void hide(SplashScreenSettings settings) {
        hide(settings.getFadeOutDuration(), false);
    }

    /**
     * Hide the Splash Screen when showing it as a dialog
     *
     * @param activity the activity showing the dialog
     */
    public void hideDialog(final AppCompatActivity activity) {
        hideDialog(activity, false);
    }

    public void onPause() {
        tearDown(true);
    }

    public void onDestroy() {
        tearDown(true);
    }

    private void buildViews() {
        if (splashImage == null) {
            int splashId = 0;
            Drawable splash;

            if (config.getLayoutName() != null) {
                splashId = context.getResources().getIdentifier(config.getLayoutName(), "layout", context.getPackageName());
                if (splashId == 0) {
                    Logger.warn("Layout not found, defaulting to ImageView");
                }
            }

            if (splashId != 0) {
                Activity activity = (Activity) context;
                LayoutInflater inflator = activity.getLayoutInflater();
                ViewGroup root = new FrameLayout(context);
                root.setLayoutParams(
                    new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                );
                splashImage = inflator.inflate(splashId, root, false);
            } else {
                splash = getSplashDrawable();
                if (splash != null) {
                    if (splash instanceof Animatable) {
                        ((Animatable) splash).start();
                    }

                    if (splash instanceof LayerDrawable) {
                        LayerDrawable layeredSplash = (LayerDrawable) splash;

                        for (int i = 0; i < layeredSplash.getNumberOfLayers(); i++) {
                            Drawable layerDrawable = layeredSplash.getDrawable(i);

                            if (layerDrawable instanceof Animatable) {
                                ((Animatable) layerDrawable).start();
                            }
                        }
                    }

                    splashImage = new ImageView(context);
                    // Stops flickers dead in their tracks
                    // https://stackoverflow.com/a/21847579/32140
                    ImageView imageView = (ImageView) splashImage;
                    imageView.setDrawingCacheEnabled(true);
                    imageView.setScaleType(config.getScaleType());
                    imageView.setImageDrawable(splash);
                }
            }

            splashImage.setFitsSystemWindows(true);

            if (config.isImmersive()) {
                final int flags =
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                splashImage.setSystemUiVisibility(flags);
            } else if (config.isFullScreen()) {
                splashImage.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
            }

            if (config.getBackgroundColor() != null) {
                splashImage.setBackgroundColor(config.getBackgroundColor());
            }
        }

        if (spinnerBar == null) {
            if (config.getSpinnerStyle() != null) {
                int spinnerBarStyle = config.getSpinnerStyle();
                spinnerBar = new ProgressBar(context, null, spinnerBarStyle);
            } else {
                spinnerBar = new ProgressBar(context);
            }
            spinnerBar.setIndeterminate(true);

            Integer spinnerBarColor = config.getSpinnerColor();
            if (spinnerBarColor != null) {
                int[][] states = new int[][] {
                    new int[] { android.R.attr.state_enabled }, // enabled
                    new int[] { -android.R.attr.state_enabled }, // disabled
                    new int[] { -android.R.attr.state_checked }, // unchecked
                    new int[] { android.R.attr.state_pressed } // pressed
                };
                int[] colors = new int[] { spinnerBarColor, spinnerBarColor, spinnerBarColor, spinnerBarColor };
                ColorStateList colorStateList = new ColorStateList(states, colors);
                spinnerBar.setIndeterminateTintList(colorStateList);
            }
        }
    }

    private Drawable getSplashDrawable() {
        int splashId = context.getResources().getIdentifier(config.getResourceName(), "drawable", context.getPackageName());
        try {
            Drawable drawable = context.getResources().getDrawable(splashId, context.getTheme());
            return drawable;
        } catch (Resources.NotFoundException ex) {
            Logger.warn("No splash screen found, not displaying");
            return null;
        }
    }

    private void show(
        final AppCompatActivity activity,
        final SplashScreenSettings settings,
        final SplashListener splashListener,
        final boolean isLaunchSplash
    ) {
        windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);

        if (activity.isFinishing()) {
            return;
        }

        buildViews();

        if (isVisible) {
            splashListener.completed();
            return;
        }

        final Animator.AnimatorListener listener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                isVisible = true;

                if (settings.isAutoHide()) {
                    new Handler()
                        .postDelayed(
                            () -> {
                                hide(settings.getFadeOutDuration(), isLaunchSplash);

                                if (splashListener != null) {
                                    splashListener.completed();
                                }
                            },
                            settings.getShowDuration()
                        );
                } else {
                    // If no autoHide, call complete
                    if (splashListener != null) {
                        splashListener.completed();
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {}

            @Override
            public void onAnimationRepeat(Animator animator) {}

            @Override
            public void onAnimationStart(Animator animator) {}
        };

        Handler mainHandler = new Handler(context.getMainLooper());

        mainHandler.post(
            () -> {
                WindowManager.LayoutParams params = new WindowManager.LayoutParams();
                params.gravity = Gravity.CENTER;
                params.flags = activity.getWindow().getAttributes().flags;

                // Required to enable the view to actually fade
                params.format = PixelFormat.TRANSLUCENT;

                try {
                    windowManager.addView(splashImage, params);
                } catch (IllegalStateException | IllegalArgumentException ex) {
                    Logger.debug("Could not add splash view");
                    return;
                }

                splashImage.setAlpha(0f);

                splashImage
                    .animate()
                    .alpha(1f)
                    .setInterpolator(new LinearInterpolator())
                    .setDuration(settings.getFadeInDuration())
                    .setListener(listener)
                    .start();

                splashImage.setVisibility(View.VISIBLE);

                if (spinnerBar != null) {
                    spinnerBar.setVisibility(View.INVISIBLE);

                    if (spinnerBar.getParent() != null) {
                        windowManager.removeView(spinnerBar);
                    }

                    params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    params.width = WindowManager.LayoutParams.WRAP_CONTENT;

                    windowManager.addView(spinnerBar, params);

                    if (config.isShowSpinner()) {
                        spinnerBar.setAlpha(0f);

                        spinnerBar
                            .animate()
                            .alpha(1f)
                            .setInterpolator(new LinearInterpolator())
                            .setDuration(settings.getFadeInDuration())
                            .start();

                        spinnerBar.setVisibility(View.VISIBLE);
                    }
                }
            }
        );
    }

    private void hide(final int fadeOutDuration, boolean isLaunchSplash) {
        // Warn the user if the splash was hidden automatically, which means they could be experiencing an app
        // that feels slower than it actually is.
        if (isLaunchSplash && isVisible) {
            Logger.debug(
                "SplashScreen was automatically hidden after the launch timeout. " +
                "You should call `SplashScreen.hide()` as soon as your web app is loaded (or increase the timeout)." +
                "Read more at https://capacitorjs.com/docs/apis/splash-screen#hiding-the-splash-screen"
            );
        }

        if (isHiding || splashImage == null || splashImage.getParent() == null) {
            return;
        }

        isHiding = true;

        final Animator.AnimatorListener listener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                tearDown(false);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                tearDown(false);
            }

            @Override
            public void onAnimationStart(Animator animator) {}

            @Override
            public void onAnimationRepeat(Animator animator) {}
        };

        Handler mainHandler = new Handler(context.getMainLooper());

        mainHandler.post(
            () -> {
                if (spinnerBar != null) {
                    spinnerBar.setAlpha(1f);

                    spinnerBar.animate().alpha(0).setInterpolator(new LinearInterpolator()).setDuration(fadeOutDuration).start();
                }

                splashImage.setAlpha(1f);

                splashImage
                    .animate()
                    .alpha(0)
                    .setInterpolator(new LinearInterpolator())
                    .setDuration(fadeOutDuration)
                    .setListener(listener)
                    .start();
            }
        );
    }

    private void hideDialog(final AppCompatActivity activity, boolean isLaunchSplash) {
        // Warn the user if the splash was hidden automatically, which means they could be experiencing an app
        // that feels slower than it actually is.
        if (isLaunchSplash && isVisible) {
            Logger.debug(
                "SplashScreen was automatically hidden after the launch timeout. " +
                "You should call `SplashScreen.hide()` as soon as your web app is loaded (or increase the timeout)." +
                "Read more at https://capacitorjs.com/docs/apis/splash-screen#hiding-the-splash-screen"
            );
        }

        if (isHiding) {
            return;
        }

        isHiding = true;

        activity.runOnUiThread(
            () -> {
                if (dialog != null && dialog.isShowing()) {
                    if (!activity.isFinishing() && !activity.isDestroyed()) {
                        dialog.dismiss();
                    }
                    dialog = null;
                    isVisible = false;
                }
            }
        );
    }

    private void tearDown(boolean removeSpinner) {
        if (spinnerBar != null && spinnerBar.getParent() != null) {
            spinnerBar.setVisibility(View.INVISIBLE);

            if (removeSpinner) {
                windowManager.removeView(spinnerBar);
            }
        }

        if (splashImage != null && splashImage.getParent() != null) {
            splashImage.setVisibility(View.INVISIBLE);

            windowManager.removeView(splashImage);
        }

        isHiding = false;
        isVisible = false;
    }
}
