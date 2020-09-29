declare module '@capacitor/core' {
  interface PluginRegistry {
    Toast: ToastPlugin;
  }
}

export interface ToastShowOptions {
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
   * Postion of the Toast
   *
   * @default 'bottom'
   * @since 1.0.0
   */
  position?: 'top' | 'center' | 'bottom';
}

export interface ToastPlugin {
  /**
   * Shows a Toast on the screen
   *
   * @since 1.0.0
   */
  show(options: ToastShowOptions): Promise<void>;
}
