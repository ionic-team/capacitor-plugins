export type OperatingSystem = 'ios' | 'android' | 'windows' | 'mac' | 'unknown';

export interface DeviceId {
  /**
   * The identifier of the device as available to the app. This identifier may change
   * on modern mobile platforms that only allow per-app install ids.
   *
   * On iOS, the identifier is a UUID that uniquely identifies a device to the app’s vendor ([read more](https://developer.apple.com/documentation/uikit/uidevice/1620059-identifierforvendor)).
   *
   * on Android 8+, __the identifier is a 64-bit number (expressed as a hexadecimal string)__, unique to each combination of app-signing key, user, and device ([read more](https://developer.android.com/reference/android/provider/Settings.Secure#ANDROID_ID)).
   *
   * On web, a random identifier is generated and stored on localStorage for subsequent calls.
   * If localStorage is not available a new random identifier will be generated on every call.
   *
   * @since 1.0.0
   */
  identifier: string;
}

export interface DeviceInfo {
  /**
   * The name of the device. For example, "John's iPhone".
   *
   * This is only supported on iOS and Android 7.1 or above.
   *
   * On iOS 16+ this will return a generic device name without the appropriate [entitlements](https://developer.apple.com/documentation/bundleresources/entitlements/com_apple_developer_device-information_user-assigned-device-name).
   *
   * @since 1.0.0
   */
  name?: string;

  /**
   * The device model. For example, "iPhone13,4".
   *
   * @since 1.0.0
   */
  model: string;

  /**
   * The device platform (lowercase).
   *
   * @since 1.0.0
   */
  platform: 'ios' | 'android' | 'web';

  /**
   * The operating system of the device.
   *
   * @since 1.0.0
   */
  operatingSystem: OperatingSystem;

  /**
   * The version of the device OS.
   *
   * @since 1.0.0
   */
  osVersion: string;

  /**
   * The iOS version number.
   *
   * Only available on iOS.
   *
   * Multi-part version numbers are crushed down into an integer padded to two-digits, ex: `"16.3.1"` -> `160301`
   *
   * @since 5.0.0
   */
  iOSVersion?: number;

  /**
   * The Android SDK version number.
   *
   * Only available on Android.
   *
   * @since 5.0.0
   */
  androidSDKVersion?: number;

  /**
   * The manufacturer of the device.
   *
   * @since 1.0.0
   */
  manufacturer: string;

  /**
   * Whether the app is running in a simulator/emulator.
   *
   * @since 1.0.0
   */
  isVirtual: boolean;

  /**
   * Approximate memory used by the current app, in bytes. Divide by
   * 1048576 to get the number of MBs used.
   *
   * @since 1.0.0
   */
  memUsed?: number;

  /**
   * How much free disk space is available on the normal data storage
   * path for the os, in bytes.
   *
   * On Android it returns the free disk space on the "system"
   * partition holding the core Android OS.
   * On iOS this value is not accurate.
   *
   * @deprecated Use `realDiskFree`.
   * @since 1.0.0
   */
  diskFree?: number;

  /**
   * The total size of the normal data storage path for the OS, in bytes.
   *
   * On Android it returns the disk space on the "system"
   * partition holding the core Android OS.
   *
   * @deprecated Use `realDiskTotal`.
   * @since 1.0.0
   */
  diskTotal?: number;

  /**
   * How much free disk space is available on the normal data storage, in bytes.
   *
   * @since 1.1.0
   */
  realDiskFree?: number;

  /**
   * The total size of the normal data storage path, in bytes.
   *
   * @since 1.1.0
   */
  realDiskTotal?: number;

  /**
   * The web view browser version
   *
   * @since 1.0.0
   */
  webViewVersion: string;
}

export interface BatteryInfo {
  /**
   * A percentage (0 to 1) indicating how much the battery is charged.
   *
   * @since 1.0.0
   */
  batteryLevel?: number;

  /**
   * Whether the device is charging.
   *
   * @since 1.0.0
   */
  isCharging?: boolean;
}

export interface GetLanguageCodeResult {
  /**
   * Two character language code.
   *
   * @since 1.0.0
   */
  value: string;
}

export interface LanguageTag {
  /**
   * Returns a well-formed IETF BCP 47 language tag.
   *
   * @since 4.0.0
   */
  value: string;
}

export interface DevicePlugin {
  /**
   * Return an unique identifier for the device.
   *
   * @since 1.0.0
   */
  getId(): Promise<DeviceId>;

  /**
   * Return information about the underlying device/os/platform.
   *
   * @since 1.0.0
   */
  getInfo(): Promise<DeviceInfo>;

  /**
   * Return information about the battery.
   *
   * @since 1.0.0
   */
  getBatteryInfo(): Promise<BatteryInfo>;

  /**
   * Get the device's current language locale code.
   *
   * @since 1.0.0
   */
  getLanguageCode(): Promise<GetLanguageCodeResult>;

  /**
   * Get the device's current language locale tag.
   *
   * @since 4.0.0
   */
  getLanguageTag(): Promise<LanguageTag>;
}

/**
 * @deprecated Use `BatteryInfo`.
 * @since 1.0.0
 */
export type DeviceBatteryInfo = BatteryInfo;

/**
 * @deprecated Use `GetLanguageCodeResult`.
 * @since 1.0.0
 */
export type DeviceLanguageCodeResult = GetLanguageCodeResult;
