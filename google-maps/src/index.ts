/* eslint-disable @typescript-eslint/no-namespace */
import { MapType, Marker } from './definitions';
import { GoogleMap } from './map';

export { GoogleMap, MapType, Marker };

declare global {
  export namespace JSX {
    export interface IntrinsicElements {
      'capacitor-google-map': any;
    }
  }
}
