export interface ToastPlugin {
  /**
   * Shows a Toast on the screen
   *
   * @since 1.0.0
   */
  show(options: ShowOptions): Promise<void>;
}

export interface ShowOptions {
  /**
   * Text to display on the Toast
   *
   * @since 1.0.0
   */
  text: string;

  /**
   * Duration of the Toast, either 'short' (2000ms) or 'long' (3500ms)
   *
   * @default 'short'
   * @since 1.0.0
   */
  duration?: 'short' | 'long';

  /**
   * Duration of the Toast in milliseconds.
   *
   * This option is only supported on iOS and Web.
   * 
   * @since 6.0.0
   */
  durationMilliseconds?: number;

  /**
   * Position of the Toast.
   *
   * On Android 12 and newer all toasts are shown at the bottom.
   *
   * @default 'bottom'
   * @since 1.0.0
   */
  position?: 'top' | 'center' | 'bottom';
}

/**
 * @deprecated Use `ToastShowOptions`.
 * @since 1.0.0
 */
export type ToastShowOptions = ShowOptions;
