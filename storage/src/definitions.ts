declare module '@capacitor/core' {
  interface PluginRegistry {
    Storage: StoragePlugin;
  }
}

export interface StoragePlugin {
  /**
   * Get the value with the given key.
   */
  get(options: { key: string }): Promise<{ value: string | null }>;
  /**
   * Set the value for the given key
   */
  set(options: { key: string; value: string }): Promise<void>;
  /**
   * Remove the value for this key (if any)
   */
  remove(options: { key: string }): Promise<void>;
  /**
   * Clear stored keys and values.
   */
  clear(): Promise<void>;
  /**
   * Return the list of known keys
   */
  keys(): Promise<{ keys: string[] }>;
}
