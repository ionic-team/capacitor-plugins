export type CallbackID = string;

export type GeolocationState = PermissionState;

export interface GeolocationPermissionStatus {
  location: GeolocationState;
}

export interface GeolocationPlugin {
  /**
   * Get the current GPS location of the device
   *
   * @since 1.0.0
   */
  getCurrentPosition(
    options?: GeolocationOptions,
  ): Promise<GeolocationPosition>;

  /**
   * Set up a watch for location changes. Note that watching for location changes
   * can consume a large amount of energy. Be smart about listening only when you need to.
   *
   * @since 1.0.0
   */
  watchPosition(
    options: GeolocationOptions,
    callback: GeolocationWatchCallback,
  ): CallbackID;

  /**
   * Clear a given watch
   *
   * @since 1.0.0
   */
  clearWatch(options: { id: string }): Promise<void>;

  /**
   * Check location permissions
   *
   * @since 1.0.0
   */
  checkPermissions(): Promise<GeolocationPermissionStatus>;

  /**
   * Request location permissions
   *
   * @since 1.0.0
   */
  requestPermissions(): Promise<GeolocationPermissionStatus>;
}

export interface GeolocationPosition {
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

export interface GeolocationOptions {
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

export type GeolocationWatchCallback = (
  position: GeolocationPosition | null,
  err?: any,
) => void;

export interface PermissionsRequestResult {
  results: any[];
}
