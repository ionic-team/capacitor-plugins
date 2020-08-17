declare module '@capacitor/core' {
  interface PluginRegistry {
    Storage: StoragePlugin;
  }
}

export interface ConfigureOptions {
  /**
   * Set the storage group.
   *
   * Storage groups are used to organize key/value pairs.
   *
   * Using the value 'NativeStorage' provides backwards-compatibility with
   * [`cordova-plugin-nativestorage`](https://www.npmjs.com/package/cordova-plugin-nativestorage).
   * WARNING: The `clear()` method can delete unintended values when using the
   * 'NativeStorage' group.
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

export interface MigrateResult {
  /**
   * An array of keys that were migrated.
   *
   * @since 0.0.1
   */
  migrated: string[];

  /**
   * An array of keys that were already migrated or otherwise exist in storage
   * that had a value in the Capacitor 2 Storage plugin.
   *
   * @since 0.0.1
   */
  existing: string[];
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

  /**
   * Migrate data from the Capacitor 2 Storage plugin.
   *
   * This action is non-destructive. It will not remove old data and will only
   * write new data if they key was not already set.
   *
   * @since 0.0.1
   */
  migrate(): Promise<MigrateResult>;
}
