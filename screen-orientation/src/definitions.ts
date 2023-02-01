import type { PluginListenerHandle } from '@capacitor/core';

export interface OrientationLockOptions {
  orientation: OrientationLockType;
}

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
  ): Promise<PluginListenerHandle> & PluginListenerHandle;

  /**
   * Removes all listeners.
   *
   * @since 4.0.0
   */
  removeAllListeners(): Promise<void>;
}
