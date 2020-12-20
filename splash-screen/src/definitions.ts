/// <reference types="@capacitor/cli" />

declare module '@capacitor/cli' {
  export interface PluginsConfig {
    SplashScreen?: {
      /**
       * How long to show the launch splash screen when autoHide is enabled (in ms)
       *
       * @since 1.0.0
       * @default 0
       */
      launchShowDuration?: number;

      /**
       * Whether to auto hide the splash after launchShowDuration
       *
       * @since 1.0.0
       * @default true
       */
      launchAutoHide?: boolean;

      /**
       * Color of the background of the Splash Screen in hex format, #RRGGBB or #RRGGBBAA
       *
       * @since 1.0.0
       */
      backgroundColor?: string;

      /**
       * Name of the resource to be used as Splash Screen
       *
       * Only available on Android
       *
       * @since 1.0.0
       * @default splash
       */
      androidSplashResourceName?: string;

      /**
       * The [ImageView.ScaleType](https://developer.android.com/reference/android/widget/ImageView.ScaleType) used to scale
       * the Splash Screen image
       *
       * Only available on Android
       *
       * @since 1.0.0
       * @default FIT_XY
       */
      androidScaleType?:
        | 'CENTER'
        | 'CENTER_CROP'
        | 'CENTER_INSIDE'
        | 'FIT_CENTER'
        | 'FIT_END'
        | 'FIT_START'
        | 'FIT_XY'
        | 'MATRIX';

      /**
       * Show a loading spinner on the Splash Screen
       *
       * @since 1.0.0
       */
      showSpinner?: boolean;

      /**
       * Style of the Android spinner
       *
       * @since 1.0.0
       * @default large
       */
      androidSpinnerStyle?:
        | 'horizontal'
        | 'small'
        | 'large'
        | 'inverse'
        | 'smallInverse'
        | 'largeInverse';

      /**
       * Style of the iOS spinner
       *
       * Only available on iOS.
       *
       * @since 1.0.0
       * @default large
       */
      iosSpinnerStyle?: 'large' | 'small';

      /**
       * Color of the spinner in hex format, #RRGGBB or #RRGGBBAA
       *
       * @since 1.0.0
       */
      spinnerColor?: string;

      /**
       * Hide the status bar on the Splash Screen
       *
       * Only available on Android
       *
       * @since 1.0.0
       */
      splashFullScreen?: boolean;

      /**
       * Hide the status bar and the software navigation buttons on the Splash Screen
       *
       * Only available on Android
       *
       * @since 1.0.0
       */
      splashImmersive?: boolean;
    };
  }
}

export interface ShowOptions {
  /**
   * Whether to auto hide the splash after showDuration
   *
   * @since 1.0.0
   */
  autoHide?: boolean;
  /**
   * How long (in ms) to fade in.
   *
   * @since 1.0.0
   * @default 200
   */
  fadeInDuration?: number;
  /**
   * How long (in ms) to fade out.
   *
   * @since 1.0.0
   * @default 200
   */
  fadeOutDuration?: number;
  /**
   * How long to show the splash screen when autoHide is enabled (in ms)
   *
   * @since 1.0.0
   * @default 3000
   */
  showDuration?: number;
}

export interface HideOptions {
  /**
   * How long (in ms) to fade out.
   *
   * @since 1.0.0
   * @default 200
   */
  fadeOutDuration?: number;
}

export interface SplashScreenPlugin {
  /**
   * Show the splash screen
   *
   * @since 1.0.0
   */
  show(options?: ShowOptions): Promise<void>;
  /**
   * Hide the splash screen
   *
   * @since 1.0.0
   */
  hide(options?: HideOptions): Promise<void>;
}

/**
 * @deprecated Use `ShowOptions`.
 * @since 1.0.0
 */
export type SplashScreenShowOptions = ShowOptions;

/**
 * @deprecated Use `HideOptions`.
 * @since 1.0.0
 */
export type SplashScreenHideOptions = HideOptions;
