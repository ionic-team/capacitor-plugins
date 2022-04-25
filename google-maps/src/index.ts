/* eslint-disable @typescript-eslint/no-namespace */
import { MapType } from './definitions';
import { GoogleMap, Marker } from './map';

export { GoogleMap, MapType, Marker };

declare global {
  export namespace JSX {
    export interface IntrinsicElements {
      'capacitor-google-map': any;
    }
  }
}
