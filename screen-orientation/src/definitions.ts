import type { PluginListenerHandle } from '@capacitor/core';

export interface OrientationLockOptions {
  /**
   * Note: Typescript v5.2+ users should import OrientationLockType from @capacitor/screen-orientation.
   */
  orientation: OrientationLockType;
}

export type OrientationLockType =
  | 'any'
  | 'natural'
  | 'landscape'
  | 'portrait'
  | 'portrait-primary'
  | 'portrait-secondary'
  | 'landscape-primary'
  | 'landscape-secondary';

export interface ScreenOrientationResult {
  type: OrientationType;
}

export interface ScreenOrientationPlugin {
  /**
   * Returns the current screen orientation.
   *
   * @since 4.0.0
   */
  orientation(): Promise<ScreenOrientationResult>;

  /**
   * Locks the screen orientation.
   *
   * Starting in Android targetSdk 36, this method has no effect for large screens (e.g. tablets) on Android 16 and higher.
   * You may opt-out of this behavior in your app by adding `<property android:name="android.window.PROPERTY_COMPAT_ALLOW_RESTRICTED_RESIZABILITY" android:value="true" />` to your `AndroidManifest.xml` inside `<application>` or `<activity>`.
   * Keep in mind though that this opt-out is temporary and will no longer work for Android 17. Android discourages setting specific orientations for large screens.
   * Regular Android phones are unaffected by this change.
   * For more information check the Android docs at https://developer.android.com/about/versions/16/behavior-changes-16#adaptive-layouts
   *
   * @since 4.0.0
   */
  lock(options: OrientationLockOptions): Promise<void>;

  /**
   * Unlocks the screen's orientation.
   *
   * @since 4.0.0
   */
  unlock(): Promise<void>;

  /**
   * Listens for screen orientation changes.
   *
   * @since 4.0.0
   */
  addListener(
    eventName: 'screenOrientationChange',
    listenerFunc: (orientation: ScreenOrientationResult) => void,
  ): Promise<PluginListenerHandle>;

  /**
   * Removes all listeners.
   *
   * @since 4.0.0
   */
  removeAllListeners(): Promise<void>;
}
