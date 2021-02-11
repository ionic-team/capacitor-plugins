/// <reference types="@capacitor/cli" />

import type { PluginListenerHandle } from '@capacitor/core';

declare module '@capacitor/cli' {
  export interface PluginsConfig {
    Keyboard?: {
      /**
       * Configure the way the app is resized when the Keyboard appears.
       *
       * Only available on iOS.
       *
       * @since 1.0.0
       * @default native
       */
      resize?: 'none' | 'native' | 'body' | 'ionic';

      /**
       * Use the dark style keyboard instead of the regular one.
       *
       * Only available on iOS.
       *
       * @since 1.0.0
       */
      style?: 'dark';
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
   * Resizes the html body.
   *
   * @since 1.0.0
   */
  Body = 'body',

  /**
   * Resizes Ionic app
   *
   * @since 1.0.0
   */
  Ionic = 'ionic',

  /**
   * Resizes the WebView.
   *
   * @since 1.0.0
   */
  Native = 'native',

  /**
   * Don't resize anything.
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
   * Listen for when the keyboard is about to be shown.
   *
   * @since 1.0.0
   */
  addListener(
    eventName: 'keyboardWillShow',
    listenerFunc: (info: KeyboardInfo) => void,
  ): Promise<PluginListenerHandle> & PluginListenerHandle;

  /**
   * Listen for when the keyboard is shown.
   *
   * @since 1.0.0
   */
  addListener(
    eventName: 'keyboardDidShow',
    listenerFunc: (info: KeyboardInfo) => void,
  ): Promise<PluginListenerHandle> & PluginListenerHandle;

  /**
   * Listen for when the keyboard is about to be hidden.
   *
   * @since 1.0.0
   */
  addListener(
    eventName: 'keyboardWillHide',
    listenerFunc: () => void,
  ): Promise<PluginListenerHandle> & PluginListenerHandle;

  /**
   * Listen for when the keyboard is hidden.
   *
   * @since 1.0.0
   */
  addListener(
    eventName: 'keyboardDidHide',
    listenerFunc: () => void,
  ): Promise<PluginListenerHandle> & PluginListenerHandle;

  /**
   * Remove all native listeners for this plugin.
   *
   * @since 1.0.0
   */
  removeAllListeners(): Promise<void>;
}
