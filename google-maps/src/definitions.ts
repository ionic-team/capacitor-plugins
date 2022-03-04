export interface LatLng {
  lat: number;
  lng: number;
}

export interface GoogleMapConfig {
  width: number;
  height: number;
  x: number;
  y: number;
  center: LatLng;
  zoom: number;
  androidLiteMode: boolean;
}

export interface CameraConfig {
  coordinate?: LatLng;
  zoom?: number;
  bearing?: number;
  angle?: number;
  animate?: boolean;
  animationDuration?: number;
}

export enum MapType {
  Normal = 'Normal',
  Hybrid = 'Hybrid',
  Satellite = 'Satellite',
  Terrain = 'Terrain',
  None = 'None',
}

export interface MapPadding {
  top: number;
  left: number;
  right: number;
  bottom: number;
}
