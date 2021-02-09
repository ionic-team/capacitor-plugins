import type { PluginListenerHandle } from '@capacitor/core';

export interface AppInfo {
  /**
   * The name of the app.
   *
   * @since 1.0.0
   */
  name: string;

  /**
   * The identifier of the app.
   * On iOS it's the Bundle Identifier.
   * On Android it's the Application ID
   *
   * @since 1.0.0
   */
  id: string;

  /**
   * The build version.
   * On iOS it's the CFBundleVersion.
   * On Android it's the versionCode.
   *
   * @since 1.0.0
   */
  build: string;

  /**
   * The app version.
   * On iOS it's the CFBundleShortVersionString.
   * On Android it's package's versionName.
   *
   * @since 1.0.0
   */
  version: string;
}

export interface AppState {
  /**
   * Whether the app is active or not.
   *
   * @since 1.0.0
   */
  isActive: boolean;
}

export interface URLOpenListenerEvent {
  /**
   * The URL the app was opened with.
   *
   * @since 1.0.0
   */
  url: string;

  /**
   * The source application opening the app (iOS only)
   * https://developer.apple.com/documentation/uikit/uiapplicationopenurloptionskey/1623128-sourceapplication
   *
   * @since 1.0.0
   */
  iosSourceApplication?: any;
  /**
   * Whether the app should open the passed document in-place
   * or must copy it first.
   * https://developer.apple.com/documentation/uikit/uiapplicationopenurloptionskey/1623123-openinplace
   *
   * @since 1.0.0
   */
  iosOpenInPlace?: boolean;
}

export interface AppLaunchUrl {
  /**
   * The url used to open the app.
   *
   * @since 1.0.0
   */
  url: string;
}

export interface RestoredListenerEvent {
  /**
   * The pluginId this result corresponds to. For example, `Camera`.
   *
   * @since 1.0.0
   */
  pluginId: string;
  /**
   * The methodName this result corresponds to. For example, `getPhoto`
   *
   * @since 1.0.0
   */
  methodName: string;
  /**
   * The result data passed from the plugin. This would be the result you'd
   * expect from normally calling the plugin method. For example, `CameraPhoto`
   *
   * @since 1.0.0
   */
  data?: any;
  /**
   * Boolean indicating if the plugin call succeeded.
   *
   * @since 1.0.0
   */
  success: boolean;
  /**
   * If the plugin call didn't succeed, it will contain the error message.
   *
   * @since 1.0.0
   */
  error?: {
    message: string;
  };
}

export type StateChangeListener = (state: AppState) => void;
export type URLOpenListener = (event: URLOpenListenerEvent) => void;
export type RestoredListener = (event: RestoredListenerEvent) => void;
export type BackButtonListener = () => void;

export interface AppPlugin {
  /**
   * Force exit the app. This should only be used in conjunction with the `backButton` handler for Android to
   * exit the app when navigation is complete.
   *
   * Ionic handles this itself so you shouldn't need to call this if using Ionic.
   *
   * @since 1.0.0
   */
  exitApp(): never;

  /**
   * Return information about the app.
   *
   * @since 1.0.0
   */
  getInfo(): Promise<AppInfo>;

  /**
   * Gets the current app state.
   *
   * @since 1.0.0
   */
  getState(): Promise<AppState>;

  /**
   * Get the URL the app was launched with, if any.
   *
   * @since 1.0.0
   */
  getLaunchUrl(): Promise<AppLaunchUrl>;

  /**
   * Listen for changes in the App's active state (whether the app is in the foreground or background)
   *
   * @since 1.0.0
   */
  addListener(
    eventName: 'appStateChange',
    listenerFunc: StateChangeListener,
  ): Promise<PluginListenerHandle> & PluginListenerHandle;

  /**
   * Listen for url open events for the app. This handles both custom URL scheme links as well
   * as URLs your app handles (Universal Links on iOS and App Links on Android)
   *
   * @since 1.0.0
   */
  addListener(
    eventName: 'appUrlOpen',
    listenerFunc: URLOpenListener,
  ): Promise<PluginListenerHandle> & PluginListenerHandle;

  /**
   * If the app was launched with previously persisted plugin call data, such as on Android
   * when an activity returns to an app that was closed, this call will return any data
   * the app was launched with, converted into the form of a result from a plugin call.
   *
   * On Android, due to memory constraints on low-end devices, it's possible
   * that, if your app launches a new activity, your app will be terminated by
   * the operating system in order to reduce memory consumption.
   *
   * For example, that means the Camera API, which launches a new Activity to
   * take a photo, may not be able to return data back to your app.
   *
   * To avoid this, Capacitor stores all restored activity results on launch.
   * You should add a listener for `appRestoredResult` in order to handle any
   * plugin call results that were delivered when your app was not running.
   *
   * Once you have that result (if any), you can update the UI to restore a
   * logical experience for the user, such as navigating or selecting the
   * proper tab.
   *
   * We recommend every Android app using plugins that rely on external
   * Activities (for example, Camera) to have this event and process handled.
   *
   * @since 1.0.0
   */
  addListener(
    eventName: 'appRestoredResult',
    listenerFunc: RestoredListener,
  ): Promise<PluginListenerHandle> & PluginListenerHandle;

  /**
   * Listen for the hardware back button event (Android only). Listening for this event will disable the
   * default back button behaviour, so you might want to call `window.history.back()` manually.
   * If you want to close the app, call `App.exitApp()`.
   *
   * @since 1.0.0
   */
  addListener(
    eventName: 'backButton',
    listenerFunc: BackButtonListener,
  ): Promise<PluginListenerHandle> & PluginListenerHandle;

  /**
   * Remove all native listeners for this plugin
   *
   * @since 1.0.0
   */
  removeAllListeners(): Promise<void>;
}

/**
 * @deprecated Use `RestoredListenerEvent`.
 * @since 1.0.0
 */
export type AppRestoredResult = RestoredListenerEvent;

/**
 * @deprecated Use `URLOpenListenerEvent`.
 * @since 1.0.0
 */
export type AppUrlOpen = URLOpenListenerEvent;
