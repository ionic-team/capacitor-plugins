/* eslint-disable @typescript-eslint/no-namespace */
import { MapType, Marker, Polygon, Circle } from './definitions';
import { GoogleMap } from './map';

export { GoogleMap, MapType, Marker, Polygon, Circle };

declare global {
  export namespace JSX {
    export interface IntrinsicElements {
      'capacitor-google-map': any;
    }
  }
}
