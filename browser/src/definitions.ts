import type { PluginListenerHandle } from '@capacitor/core';

export interface BrowserPlugin {
  /**
   * Open a page with the specified options.
   *
   * @since 1.0.0
   */
  open(options: OpenOptions): Promise<void>;

  /**
   * Web & iOS only: Close an open browser window.
   *
   * No-op on other platforms.
   *
   * @since 1.0.0
   */
  close(): Promise<void>;

  /**
   * Android & iOS only: Listen for the browser finished event.
   * It fires when the Browser is closed by the user.
   *
   * @since 1.0.0
   */
  addListener(
    eventName: 'browserFinished',
    listenerFunc: () => void,
  ): Promise<PluginListenerHandle>;

  /**
   * Android & iOS only: Listen for the page loaded event.
   * It's only fired when the URL passed to open method finish loading.
   * It is not invoked for any subsequent page loads.
   *
   * @since 1.0.0
   */
  addListener(
    eventName: 'browserPageLoaded',
    listenerFunc: () => void,
  ): Promise<PluginListenerHandle>;

  /**
   * Remove all native listeners for this plugin.
   *
   * @since 1.0.0
   */
  removeAllListeners(): Promise<void>;
}

/**
 * Represents the options passed to `open`.
 *
 * @since 1.0.0
 */
export interface OpenOptions {
  /**
   * The URL to which the browser is opened.
   *
   * @since 1.0.0
   */
  url: string;

  /**
   * Web only: Optional target for browser open. Follows
   * the `target` property for window.open. Defaults
   * to _blank.
   *
   * Ignored on other platforms.
   *
   * @since 1.0.0
   */
  windowName?: string;

  /**
   * A hex color to which the toolbar color is set.
   *
   * @since 1.0.0
   */
  toolbarColor?: string;

  /**
   * iOS only: The presentation style of the browser. Defaults to fullscreen.
   *
   * Ignored on other platforms.
   *
   * @since 1.0.0
   */
  presentationStyle?: 'fullscreen' | 'popover';

  /**
   * iOS only: The width the browser when using presentationStyle 'popover' on iPads.
   *
   * Ignored on other platforms.
   *
   * @since 4.0.0
   */
  width?: number;

  /**
   * iOS only: The height the browser when using presentationStyle 'popover' on iPads.
   *
   * Ignored on other platforms.
   *
   * @since 4.0.0
   */
  height?: number;
}

/**
 * @deprecated Use `OpenOptions`.
 * @since 1.0.0
 */
export type BrowserOpenOptions = OpenOptions;
