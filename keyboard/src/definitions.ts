/// <reference types="@capacitor/cli" />

import type { PluginListenerHandle } from '@capacitor/core';

declare module '@capacitor/cli' {
  export interface PluginsConfig {
    /**
     * On iOS, the keyboard can be configured with the following options:
     */
    Keyboard?: {
      /**
       * Configure the way the app is resized when the Keyboard appears.
       *
       * Only available on iOS.
       *
       * @since 1.0.0
       * @default native
       * @example "body"
       */
      resize?: KeyboardResize;

      /**
       * Override the keyboard style if your app doesn't support dark/light theme changes.
       * If not set, the keyboard style will depend on the device appearance.
       *
       * Only available on iOS.
       *
       * @since 1.0.0
       * @example "DARK"
       */
      style?: KeyboardStyle;

      /**
       * There is an Android bug that prevents the keyboard from resizing the WebView
       * when the app is in full screen (i.e. if StatusBar plugin is used to overlay the status bar).
       * This setting, if set to true, add a workaround that resizes the WebView even when the app is in full screen.
       *
       * Only available for Android
       *
       * @since 1.1.0
       * @example true
       */
      resizeOnFullScreen?: boolean;
    };
  }
}

export interface KeyboardInfo {
  /**
   * Height of the heyboard.
   *
   * @since 1.0.0
   */
  keyboardHeight: number;
}

export interface KeyboardStyleOptions {
  /**
   * Style of the keyboard.
   *
   * @since 1.0.0
   * @default KeyboardStyle.Default
   */
  style: KeyboardStyle;
}

export enum KeyboardStyle {
  /**
   * Dark keyboard.
   *
   * @since 1.0.0
   */
  Dark = 'DARK',

  /**
   * Light keyboard.
   *
   * @since 1.0.0
   */
  Light = 'LIGHT',

  /**
   * On iOS 13 and newer the keyboard style is based on the device appearance.
   * If the device is using Dark mode, the keyboard will be dark.
   * If the device is using Light mode, the keyboard will be light.
   * On iOS 12 the keyboard will be light.
   *
   * @since 1.0.0
   */
  Default = 'DEFAULT',
}

export interface KeyboardResizeOptions {
  /**
   * Mode used to resize elements when the keyboard appears.
   *
   * @since 1.0.0
   */
  mode: KeyboardResize;
}

export enum KeyboardResize {
  /**
   * Only the `body` HTML element will be resized.
   * Relative units are not affected, because the viewport does not change.
   *
   * @since 1.0.0
   */
  Body = 'body',

  /**
   * Only the `ion-app` HTML element will be resized.
   * Use it only for Ionic Framework apps.
   *
   * @since 1.0.0
   */
  Ionic = 'ionic',

  /**
   * The whole native Web View will be resized when the keyboard shows/hides.
   * This affects the `vh` relative unit.
   *
   * @since 1.0.0
   */
  Native = 'native',

  /**
   * Neither the app nor the Web View are resized.
   *
   * @since 1.0.0
   */
  None = 'none',
}

export interface KeyboardPlugin {
  /**
   * Show the keyboard. This method is alpha and may have issues.
   *
   * This method is only supported on Android.
   *
   * @since 1.0.0
   */
  show(): Promise<void>;

  /**
   * Hide the keyboard.
   *
   * @since 1.0.0
   */
  hide(): Promise<void>;

  /**
   * Set whether the accessory bar should be visible on the keyboard. We recommend disabling
   * the accessory bar for short forms (login, signup, etc.) to provide a cleaner UI.
   *
   * This method is only supported on iPhone devices.
   *
   * @since 1.0.0
   */
  setAccessoryBarVisible(options: { isVisible: boolean }): Promise<void>;

  /**
   * Programmatically enable or disable the WebView scroll.
   *
   * This method is only supported on iOS.
   *
   * @since 1.0.0
   */
  setScroll(options: { isDisabled: boolean }): Promise<void>;

  /**
   * Programmatically set the keyboard style.
   *
   * This method is only supported on iOS.
   *
   * @since 1.0.0
   */
  setStyle(options: KeyboardStyleOptions): Promise<void>;

  /**
   * Programmatically set the resize mode.
   *
   * This method is only supported on iOS.
   *
   * @since 1.0.0
   */
  setResizeMode(options: KeyboardResizeOptions): Promise<void>;

  /**
   * Get the currently set resize mode.
   *
   * This method is only supported on iOS.
   *
   * @since 4.0.0
   */
  getResizeMode(): Promise<KeyboardResizeOptions>;

  /**
   * Listen for when the keyboard is about to be shown.
   *
   * On Android keyboardWillShow and keyboardDidShow fire almost at the same time.
   *
   * @since 1.0.0
   */
  addListener(
    eventName: 'keyboardWillShow',
    listenerFunc: (info: KeyboardInfo) => void,
  ): Promise<PluginListenerHandle>;

  /**
   * Listen for when the keyboard is shown.
   *
   * On Android keyboardWillShow and keyboardDidShow fire almost at the same time.
   *
   * @since 1.0.0
   */
  addListener(
    eventName: 'keyboardDidShow',
    listenerFunc: (info: KeyboardInfo) => void,
  ): Promise<PluginListenerHandle>;

  /**
   * Listen for when the keyboard is about to be hidden.
   *
   * On Android keyboardWillHide and keyboardDidHide fire almost at the same time.
   *
   * @since 1.0.0
   */
  addListener(
    eventName: 'keyboardWillHide',
    listenerFunc: () => void,
  ): Promise<PluginListenerHandle>;

  /**
   * Listen for when the keyboard is hidden.
   *
   * On Android keyboardWillHide and keyboardDidHide fire almost at the same time.
   *
   * @since 1.0.0
   */
  addListener(
    eventName: 'keyboardDidHide',
    listenerFunc: () => void,
  ): Promise<PluginListenerHandle>;

  /**
   * Remove all native listeners for this plugin.
   *
   * @since 1.0.0
   */
  removeAllListeners(): Promise<void>;
}
