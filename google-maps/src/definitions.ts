export interface LatLng {
  lat: number;
  lng: number;
}

export interface GoogleMapConfig {
  width?: number;
  height?: number;
  x?: number;
  y?: number;
  center: LatLng;
  zoom: number;
  androidLiteMode: boolean;
}
