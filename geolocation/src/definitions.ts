declare module '@capacitor/core' {
  interface PluginRegistry {
    Geolocation: GeolocationPlugin;
  }
}

export type CallbackID = string;

export type GeolocationPermissionType = 'location';
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
    altitudeAccuracy: number | null;

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
  enableHighAccuracy?: boolean; // default: false
  timeout?: number; // default: 10000
  maximumAge?: number; // default: 0
}

export type GeolocationWatchCallback = (
  position: GeolocationPosition | null,
  err?: any,
) => void;

export interface PermissionsRequestResult {
  results: any[];
}
