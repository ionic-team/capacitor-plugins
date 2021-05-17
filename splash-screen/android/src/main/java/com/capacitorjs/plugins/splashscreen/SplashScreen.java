package com.capacitorjs.plugins.splashscreen;

import android.animation.Animator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import com.getcapacitor.Logger;

/**
 * A Splash Screen service for showing and hiding a splash screen in the app.
 */
public class SplashScreen {

    private ImageView splashImage;
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

        show(activity, settings, null, true);
    }

    /**
     * Show the Splash Screen
     *
     * @param activity
     * @param settings Settings used to show the Splash Screen
     * @param splashListener A listener to handle the finish of the animation (if any)
     */
    public void show(final AppCompatActivity activity, final SplashScreenSettings settings, final SplashListener splashListener) {
        show(activity, settings, splashListener, false);
    }

    /**
     * Hide the Splash Screen
     *
     * @param settings Settings used to hide the Splash Screen
     */
    public void hide(SplashScreenSettings settings) {
        hide(settings.getFadeOutDuration(), false);
    }

    public void onPause() {
        tearDown(true);
    }

    public void onDestroy() {
        tearDown(true);
    }

    private void buildViews() {
        if (splashImage == null) {
            int splashId = context.getResources().getIdentifier(config.getResourceName(), "drawable", context.getPackageName());

            Drawable splash;
            try {
                splash = context.getResources().getDrawable(splashId, context.getTheme());
            } catch (Resources.NotFoundException ex) {
                Logger.warn("No splash screen found, not displaying");
                return;
            }

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

            // Stops flickers dead in their tracks
            // https://stackoverflow.com/a/21847579/32140
            splashImage.setDrawingCacheEnabled(true);

            if (config.getBackgroundColor() != null) {
                splashImage.setBackgroundColor(config.getBackgroundColor());
            }

            splashImage.setScaleType(config.getScaleType());
            splashImage.setImageDrawable(splash);
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
