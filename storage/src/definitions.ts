declare module '@capacitor/core' {
  interface PluginRegistry {
    Storage: StoragePlugin;
  }
}

export interface ConfigureOptions {
  /**
   * Set the storage group.
   *
   * Storage groups can be used to organize key/value pairs.
   *
   * @default CapacitorStorage
   * @since 0.0.1
   */
  group?: string;
}

export interface GetOptions {
  /**
   * The key whose value to retrieve from storage.
   *
   * @since 0.0.1
   */
  key: string;
}

export interface GetResult {
  /**
   * The value from storage associated with the given key.
   *
   * If a value was not previously set or was removed, value will be `null`.
   *
   * @since 0.0.1
   */
  value: string | null;
}

export interface SetOptions {
  /**
   * The key to associate with the value being set in storage.
   *
   * @since 0.0.1
   */
  key: string;

  /**
   * The value to set in storage with the associated key.
   *
   * @since 0.0.1
   */
  value: string;
}

export interface RemoveOptions {
  /**
   * The key whose value to remove from storage.
   *
   * @since 0.0.1
   */
  key: string;
}

export interface KeysResult {
  /**
   * The known keys in storage.
   *
   * @since 0.0.1
   */
  keys: string[];
}

export interface StoragePlugin {
  /**
   * Configure the storage plugin at runtime.
   *
   * Options that are `undefined` will not be used.
   *
   * @since 0.0.1
   */
  configure(options: ConfigureOptions): Promise<void>;

  /**
   * Get the value from storage of a given key.
   *
   * @since 0.0.1
   */
  get(options: GetOptions): Promise<GetResult>;

  /**
   * Set the value in storage for a given key.
   *
   * @since 0.0.1
   */
  set(options: SetOptions): Promise<void>;

  /**
   * Remove the value from storage for a given key, if any.
   *
   * @since 0.0.1
   */
  remove(options: RemoveOptions): Promise<void>;

  /**
   * Clear keys and values from storage.
   *
   * @since 0.0.1
   */
  clear(): Promise<void>;

  /**
   * Return the list of known keys in storage.
   *
   * @since 0.0.1
   */
  keys(): Promise<KeysResult>;
}
