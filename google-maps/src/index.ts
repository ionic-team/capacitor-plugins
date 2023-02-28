/* eslint-disable @typescript-eslint/no-namespace */
import { LatLngBounds, MapType, Marker } from './definitions';
import { GoogleMap } from './map';

export { GoogleMap, LatLngBounds, MapType, Marker };

declare global {
  export namespace JSX {
    export interface IntrinsicElements {
      'capacitor-google-map': any;
    }
  }
}
