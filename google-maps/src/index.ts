import { MapType } from './definitions';
import { GoogleMap, Marker } from './map';

export { GoogleMap, MapType, Marker };

declare global {
  export namespace JSX {
    interface IntrinsicElements {
      'capacitor-google-map': any;
    }
  }
}
