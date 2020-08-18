import { PluginListenerHandle } from '@capacitor/core';

export interface BrowserPlugin {
  /**
   * Open a page with the specified options.
   *
   * @since 1.0.0
   */
  open(options: BrowserOpenOptions): Promise<void>;

  /**
   * Android only: Hints to the browser that the specified URLs will be accessed and can
   * improve initial loading times.
   *
   * No-op on other platforms.
   *
   * @since 1.0.0
   */
  prefetch(options: BrowserPrefetchOptions): Promise<void>;

  /**
   * Web & iOS only: Close an open browser window.
   *
   * No-op on other platforms.
   *
   * @since 1.0.0
   */
  close(): Promise<void>;

  /**
   * Listen for the finished event.
   *
   * @since 1.0.0
   */
  addListener(
    eventName: 'browserFinished',
    listenerFunc: (info: BrowserInfo) => void,
  ): PluginListenerHandle;

  /**
   * Listen for the page loaded event.
   *
   * @since 1.0.0
   */
  addListener(
    eventName: 'browserPageLoaded',
    listenerFunc: (info: BrowserInfo) => void,
  ): PluginListenerHandle;

  /**
   * Remove all native listeners for this plugin.
   *
   * @since 1.0.0
   */
  removeAllListeners(): void;
}

/**
 * Represents the browser information passed to event listeners.
 *
 * @since 1.0.0
 */
export interface BrowserInfo {}

/**
 * Represents the options passed to `open`.
 *
 * @since 1.0.0
 */
export interface BrowserOpenOptions {
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
}

/**
 * Represents the options passed to `prefetch`.
 *
 * @since 1.0.0
 */
export interface BrowserPrefetchOptions {
  /**
   * The URLs that should be pre-fetched if possible.
   *
   * @since 1.0.0
   */
  urls: string[];
}
