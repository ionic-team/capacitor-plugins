import { MapType } from './definitions';
import { GoogleMap, Marker } from './map';

export { GoogleMap, MapType, Marker };

// eslint-disable-next-line @typescript-eslint/no-namespace
declare global {
  export namespace JSX {
    export interface IntrinsicElements {
      'capacitor-google-map': any;
    }
  }
}
