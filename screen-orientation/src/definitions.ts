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
