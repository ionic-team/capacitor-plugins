import type { PermissionState } from '@capacitor/core';

export type CallbackID = string;

export interface PermissionStatus {
  location: PermissionState;
}

export interface GeolocationPlugin {
  /**
   * Get the current GPS location of the device
   *
   * @since 1.0.0
   */
  getCurrentPosition(options?: PositionOptions): Promise<Position>;

  /**
   * Set up a watch for location changes. Note that watching for location changes
   * can consume a large amount of energy. Be smart about listening only when you need to.
   *
   * @since 1.0.0
   */
  watchPosition(
    options: PositionOptions,
    callback: WatchPositionCallback,
  ): Promise<CallbackID>;

  /**
   * Clear a given watch
   *
   * @since 1.0.0
   */
  clearWatch(options: ClearWatchOptions): Promise<void>;

  /**
   * Check location permissions
   *
   * @since 1.0.0
   */
  checkPermissions(): Promise<PermissionStatus>;

  /**
   * Request location permissions
   *
   * @since 1.0.0
   */
  requestPermissions(): Promise<PermissionStatus>;
}

export interface ClearWatchOptions {
  id: CallbackID;
}

export interface Position {
  /**
   * Creation timestamp for coords
   *
   * @since 1.0.0
   */
  timestamp: number;

  /**
   * The GPS coordinates along with the accuracy of the data
   *
   * @since 1.0.0
   */
  coords: {
    /**
     * Latitude in decimal degrees
     *
     * @since 1.0.0
     */
    latitude: number;

    /**
     * longitude in decimal degrees
     *
     * @since 1.0.0
     */
    longitude: number;

    /**
     * Accuracy level of the latitude and longitude coordinates in meters
     *
     * @since 1.0.0
     */
    accuracy: number;

    /**
     * Accuracy level of the altitude coordinate in meters, if available.
     *
     * Available on all iOS versions and on Android 8.0+.
     *
     * @since 1.0.0
     */
    altitudeAccuracy: number | null | undefined;

    /**
     * The altitude the user is at (if available)
     *
     * @since 1.0.0
     */
    altitude: number | null;

    /**
     * The speed the user is traveling (if available)
     *
     * @since 1.0.0
     */
    speed: number | null;

    /**
     * The heading the user is facing (if available)
     *
     * @since 1.0.0
     */
    heading: number | null;
  };
}

export interface PositionOptions {
  /**
   * High accuracy mode (such as GPS, if available)
   *
   * @default false
   * @since 1.0.0
   */
  enableHighAccuracy?: boolean;

  /**
   * The maximum wait time in milliseconds for location updates
   *
   * @default 10000
   * @since 1.0.0
   */
  timeout?: number;

  /**
   * The maximum age in milliseconds of a possible cached position that is acceptable to return
   *
   * @default 0
   * @since 1.0.0
   */
  maximumAge?: number;
}

export type WatchPositionCallback = (
  position: Position | null,
  err?: any,
) => void;

/**
 * @deprecated Use `PositionOptions`.
 * @since 1.0.0
 */
export type GeolocationOptions = PositionOptions;

/**
 * @deprecated Use `WatchPositionCallback`.
 * @since 1.0.0
 */
export type GeolocationWatchCallback = WatchPositionCallback;

/**
 * @deprecated Use `Position`.
 * @since 1.0.0
 */
export type GeolocationPosition = Position;
