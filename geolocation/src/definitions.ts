declare module '@capacitor/core' {
  interface PluginRegistry {
    Geolocation: GeolocationPlugin;
  }
}

export type CallbackID = string;

export type GeolocationPermissionType = 'location';
export type GeolocationState = PermissionState;

export interface GeolocationPluginPermissionStatus {
  location: GeolocationState;
}

export interface GeolocationPlugin {
  /**
   * Get the current GPS location of the device
   */
  getCurrentPosition(
    options?: GeolocationOptions,
  ): Promise<GeolocationPosition>;
  /**
   * Set up a watch for location changes. Note that watching for location changes
   * can consume a large amount of energy. Be smart about listening only when you need to.
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
   */
  checkPermissions(): Promise<GeolocationPluginPermissionStatus>;

  /**
   * Request location permissions
   */
  requestPermissions(
    permissions: GeolocationPermissionType,
  ): Promise<GeolocationPluginPermissionStatus>;
}

export interface GeolocationPosition {
  /**
   * Creation timestamp for coords
   */
  timestamp: number;
  /**
   * The GPS coordinates along with the accuracy of the data
   */
  coords: {
    /**
     * Latitude in decimal degrees
     */
    latitude: number;
    /**
     * longitude in decimal degrees
     */
    longitude: number;
    /**
     * Accuracy level of the latitude and longitude coordinates in meters
     */
    accuracy: number;
    /**
     * Accuracy level of the altitude coordinate in meters (if available)
     */
    altitudeAccuracy: number | null;
    /**
     * The altitude the user is at (if available)
     */
    altitude: number | null;
    /**
     * The speed the user is traveling (if available)
     */
    speed: number | null;
    /**
     * The heading the user is facing (if available)
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
