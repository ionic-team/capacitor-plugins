/**
 * An interface representing a pair of latitude and longitude coordinates.
 */
export interface LatLng {
  /**
   * Coordinate latitude, in degrees. This value is in the range [-90, 90].
   */
  lat: number;

  /**
   * Coordinate longitude, in degrees. This value is in the range [-180, 180].
   */
  lng: number;
}

/**
 *
 */
export interface GoogleMapConfig {
  /**
   * Override width for native map
   */
  width?: number;
  /**
   * Override height for native map
   */
  height?: number;
  /**
   * Override absolute x coordinate position for native map
   */
  x?: number;
  /**
   * Override absolute y coordinate position for native map
   */
  y?: number;
  /**
   * Default location on the Earth towards which the camera points.
   */
  center: LatLng;
  /**
   * Sets the zoom of the map.
   */
  zoom: number;
  /**
   * Enables image-based lite mode on Android.
   *
   * @default false
   */
  androidLiteMode?: boolean;
}

/**
 * Configuration properties for a Google Map Camera
 */
export interface CameraConfig {
  /**
   * Location on the Earth towards which the camera points.
   */
  coordinate?: LatLng;
  /**
   * Sets the zoom of the map.
   */
  zoom?: number;
  /**
   * Bearing of the camera, in degrees clockwise from true north.
   *
   * @default 0
   */
  bearing?: number;
  /**
   * The angle, in degrees, of the camera from the nadir (directly facing the Earth).
   *
   * The only allowed values are 0 and 45.
   *
   * @default 0
   */
  angle?: number;
  /**
   * Animate the transition to the new Camera properties.
   *
   * @default false
   */
  animate?: boolean;

  /**
   *
   */
  animationDuration?: number;
}

export enum MapType {
  /**
   * Basic map.
   */
  Normal = 'Normal',
  /**
   * Satellite imagery with roads and labels.
   */
  Hybrid = 'Hybrid',
  /**
   * Satellite imagery with no labels.
   */
  Satellite = 'Satellite',
  /**
   * Topographic data.
   */
  Terrain = 'Terrain',
  /**
   * No base map tiles.
   */
  None = 'None',
}

/**
 * Controls for setting padding on the 'visible' region of the view.
 */
export interface MapPadding {
  top: number;
  left: number;
  right: number;
  bottom: number;
}
