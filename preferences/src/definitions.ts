export interface ConfigureOptions {
  /**
   * Set the preferences group.
   *
   * Preferences groups are used to organize key/value pairs.
   *
   * Using the value 'NativeStorage' provides backwards-compatibility with
   * [`cordova-plugin-nativestorage`](https://www.npmjs.com/package/cordova-plugin-nativestorage).
   * WARNING: The `clear()` method can delete unintended values when using the
   * 'NativeStorage' group.
   *
   * @default CapacitorStorage
   * @since 1.0.0
   */
  group?: string;
}

export interface GetOptions {
  /**
   * The key whose value to retrieve from preferences.
   *
   * @since 1.0.0
   */
  key: string;
}

export interface GetResult {
  /**
   * The value from preferences associated with the given key.
   *
   * If a value was not previously set or was removed, value will be `null`.
   *
   * @since 1.0.0
   */
  value: string | null;
}

export interface SetOptions {
  /**
   * The key to associate with the value being set in preferences.
   *
   * @since 1.0.0
   */
  key: string;

  /**
   * The value to set in preferences with the associated key.
   *
   * @since 1.0.0
   */
  value: string;
}

export interface RemoveOptions {
  /**
   * The key whose value to remove from preferences.
   *
   * @since 1.0.0
   */
  key: string;
}

export interface KeysResult {
  /**
   * The known keys in preferences.
   *
   * @since 1.0.0
   */
  keys: string[];
}

export interface MigrateResult {
  /**
   * An array of keys that were migrated.
   *
   * @since 1.0.0
   */
  migrated: string[];

  /**
   * An array of keys that were already migrated or otherwise exist in preferences
   * that had a value in the Capacitor 2 Preferences plugin.
   *
   * @since 1.0.0
   */
  existing: string[];
}

export interface PreferencesPlugin {
  /**
   * Configure the preferences plugin at runtime.
   *
   * Options that are `undefined` will not be used.
   *
   * @since 1.0.0
   */
  configure(options: ConfigureOptions): Promise<void>;

  /**
   * Get the value from preferences of a given key.
   *
   * @since 1.0.0
   */
  get(options: GetOptions): Promise<GetResult>;

  /**
   * Set the value in preferences for a given key.
   *
   * @since 1.0.0
   */
  set(options: SetOptions): Promise<void>;

  /**
   * Remove the value from preferences for a given key, if any.
   *
   * @since 1.0.0
   */
  remove(options: RemoveOptions): Promise<void>;

  /**
   * Clear keys and values from preferences.
   *
   * @since 1.0.0
   */
  clear(): Promise<void>;

  /**
   * Return the list of known keys in preferences.
   *
   * @since 1.0.0
   */
  keys(): Promise<KeysResult>;

  /**
   * Migrate data from the Capacitor 2 Storage plugin.
   *
   * This action is non-destructive. It will not remove old data and will only
   * write new data if they key was not already set.
   * To remove the old data after being migrated, call removeOld().
   *
   * @since 1.0.0
   */
  migrate(): Promise<MigrateResult>;

  /**
   * Removes old data with `_cap_` prefix from the Capacitor 2 Storage plugin.
   *
   * @since 1.1.0
   */
  removeOld(): Promise<void>;
}
