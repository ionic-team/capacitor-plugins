import { PluginListenerHandle } from '@capacitor/core';

export interface NetworkPlugin {
  /**
   * Query the current status of the network connection.
   *
   * @since 1.0.0
   */
  getStatus(): Promise<NetworkStatus>;

  /**
   * Listen for changes in the network connection.
   *
   * @since 1.0.0
   */
  addListener(
    eventName: 'networkStatusChange',
    listenerFunc: (status: NetworkStatus) => void,
  ): PluginListenerHandle;

  /**
   * Remove all listeners (including the network status changes) for this plugin.
   *
   * @since 1.0.0
   */
  removeAllListeners(): void;
}

/**
 * Represents the state and type of the network connection.
 *
 * @since 1.0.0
 */
export interface NetworkStatus {
  /**
   * Whether there is an active connection or not.
   *
   * @since 1.0.0
   */
  connected: boolean;

  /**
   * The type of network connection currently in use.
   *
   * If there is no active network connection, `connectionType` will be `'none'`.
   *
   * @since 1.0.0
   */
  connectionType: 'wifi' | 'cellular' | 'none' | 'unknown';
}

/**
 * Callback to receive the status change notifications.
 *
 * @since 1.0.0
 */
export type NetworkStatusChangeCallback = (status: NetworkStatus) => void;
