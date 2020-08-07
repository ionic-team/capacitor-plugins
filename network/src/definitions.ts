declare module '@capacitor/core' {
  interface PluginRegistry {
    Network: NetworkPlugin;
  }
}

export interface PluginListenerHandle {
  remove: () => void;
}

export interface NetworkPlugin {
  /**
   * Query the current network status
   */
  getStatus(): Promise<NetworkStatus>;

  /**
   * Listen for network status change events
   */
  addListener(
    eventName: 'networkStatusChange',
    listenerFunc: (status: NetworkStatus) => void,
  ): PluginListenerHandle;

  /**
   * Remove all native listeners for this plugin
   */
  removeAllListeners(): void;
}

export interface NetworkStatus {
  connected: boolean;
  connectionType: 'wifi' | 'cellular' | 'none' | 'unknown';
}

export type NetworkStatusChangeCallback = (status: NetworkStatus) => void;
