/* eslint-disable @typescript-eslint/no-namespace */
import {
  LatLngBounds,
  MapType,
  Marker,
  Polygon,
  Circle,
  Polyline,
  StyleSpan,
} from './definitions';
import { GoogleMap } from './map';

export {
  GoogleMap,
  LatLngBounds,
  MapType,
  Marker,
  Polygon,
  Circle,
  Polyline,
  StyleSpan,
};

declare global {
  export namespace JSX {
    export interface IntrinsicElements {
      'capacitor-google-map': any;
    }
  }
}
