import type { PluginListenerHandle } from '@capacitor/core';

export interface ScreenOrientationPlugin {
  /**
   * Returns the current screen orientation.
   *
   * @since 4.0.0
   */
  orientation(): Promise<{ type: OrientationType }>;

  /**
   * Locks the screen orientation.
   *
   * @since 4.0.0
   */
  lock(opts: { orientation: OrientationLockType }): Promise<void>;

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
    listenerFunc: (orientation: { type: OrientationType }) => void,
  ): Promise<PluginListenerHandle> & PluginListenerHandle;

  /**
   * Removes all listeners.
   *
   * @since 4.0.0
   */
  removeAllListeners(): Promise<void>;
}
