/* eslint-disable @typescript-eslint/no-namespace */
import { MapType, Marker, Polygon } from './definitions';
import { GoogleMap } from './map';

export { GoogleMap, MapType, Marker, Polygon };

declare global {
  export namespace JSX {
    export interface IntrinsicElements {
      'capacitor-google-map': any;
    }
  }
}
