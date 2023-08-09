import type { Plugin } from '@capacitor/core';
import { registerPlugin } from '@capacitor/core';

import type {
  CameraConfig,
  Circle,
  GoogleMapConfig,
  LatLng,
  LatLngBounds,
  MapPadding,
  MapType,
  Marker,
  Polygon,
  Polyline,
} from './definitions';

/**
 * An interface containing the options used when creating a map.
 */
export interface CreateMapArgs {
  /**
   * A unique identifier for the map instance.
   */
  id: string;
  /**
   * The Google Maps SDK API Key.
   */
  apiKey: string;
  /**
   * The initial configuration settings for the map.
   */
  config: GoogleMapConfig;
  /**
   * The DOM element that the Google Map View will be mounted on which determines size and positioning.
   */
  element: HTMLElement;
  /**
   * Destroy and re-create the map instance if a map with the supplied id already exists
   * @default false
   */
  forceCreate?: boolean;
  /**
   * The region parameter alters your application to serve different map tiles or bias the application (such as biasing geocoding results towards the region).
   *
   * Only available for web.
   */
  region?: string;

  /**
   * The language parameter affects the names of controls, copyright notices, driving directions, and control labels, as well as the responses to service requests.
   *
   * Only available for web.
   */
  language?: string;
}

export interface DestroyMapArgs {
  id: string;
}

export interface RemoveMarkerArgs {
  id: string;
  markerId: string;
}

export interface RemoveMarkersArgs {
  id: string;
  markerIds: string[];
}

export interface AddMarkerArgs {
  id: string;
  marker: Marker;
}

export interface AddPolygonsArgs {
  id: string;
  polygons: Polygon[];
}

export interface RemovePolygonsArgs {
  id: string;
  polygonIds: string[];
}

export interface AddCirclesArgs {
  id: string;
  circles: Circle[];
}

export interface RemoveCirclesArgs {
  id: string;
  circleIds: string[];
}
export interface AddPolylinesArgs {
  id: string;
  polylines: Polyline[];
}

export interface RemovePolylinesArgs {
  id: string;
  polylineIds: string[];
}

export interface CameraArgs {
  id: string;
  config: CameraConfig;
}

export interface MapTypeArgs {
  id: string;
  mapType: MapType;
}

export interface IndoorMapArgs {
  id: string;
  enabled: boolean;
}

export interface TrafficLayerArgs {
  id: string;
  enabled: boolean;
}

export interface AccElementsArgs {
  id: string;
  enabled: boolean;
}

export interface PaddingArgs {
  id: string;
  padding: MapPadding;
}

export interface CurrentLocArgs {
  id: string;
  enabled: boolean;
}
export interface AddMarkersArgs {
  id: string;
  markers: Marker[];
}

export interface MapBoundsArgs {
  id: string;
  mapBounds: {
    x: number;
    y: number;
    width: number;
    height: number;
  };
}

export interface MapBoundsContainsArgs {
  bounds: LatLngBounds;
  point: LatLng;
}

export type MapBoundsExtendArgs = MapBoundsContainsArgs;

export interface EnableClusteringArgs {
  id: string;
  minClusterSize?: number;
}

export interface FitBoundsArgs {
  id: string;
  bounds: LatLngBounds;
  padding?: number;
}

export interface CapacitorGoogleMapsPlugin extends Plugin {
  create(options: CreateMapArgs): Promise<void>;
  enableTouch(args: { id: string }): Promise<void>;
  disableTouch(args: { id: string }): Promise<void>;
  addMarker(args: AddMarkerArgs): Promise<{ id: string }>;
  addMarkers(args: AddMarkersArgs): Promise<{ ids: string[] }>;
  removeMarker(args: RemoveMarkerArgs): Promise<void>;
  removeMarkers(args: RemoveMarkersArgs): Promise<void>;
  addPolygons(args: AddPolygonsArgs): Promise<{ ids: string[] }>;
  removePolygons(args: RemovePolygonsArgs): Promise<void>;
  addCircles(args: AddCirclesArgs): Promise<{ ids: string[] }>;
  removeCircles(args: RemoveCirclesArgs): Promise<void>;
  addPolylines(args: AddPolylinesArgs): Promise<{ ids: string[] }>;
  removePolylines(args: RemovePolylinesArgs): Promise<void>;
  enableClustering(args: EnableClusteringArgs): Promise<void>;
  disableClustering(args: { id: string }): Promise<void>;
  destroy(args: DestroyMapArgs): Promise<void>;
  setCamera(args: CameraArgs): Promise<void>;
  getMapType(args: { id: string }): Promise<{ type: string }>;
  setMapType(args: MapTypeArgs): Promise<void>;
  enableIndoorMaps(args: IndoorMapArgs): Promise<void>;
  enableTrafficLayer(args: TrafficLayerArgs): Promise<void>;
  enableAccessibilityElements(args: AccElementsArgs): Promise<void>;
  enableCurrentLocation(args: CurrentLocArgs): Promise<void>;
  setPadding(args: PaddingArgs): Promise<void>;
  onScroll(args: MapBoundsArgs): Promise<void>;
  onResize(args: MapBoundsArgs): Promise<void>;
  onDisplay(args: MapBoundsArgs): Promise<void>;
  dispatchMapEvent(args: { id: string; focus: boolean }): Promise<void>;
  getMapBounds(args: { id: string }): Promise<LatLngBounds>;
  fitBounds(args: FitBoundsArgs): Promise<void>;
  mapBoundsContains(
    args: MapBoundsContainsArgs,
  ): Promise<{ contains: boolean }>;
  mapBoundsExtend(args: MapBoundsExtendArgs): Promise<{ bounds: LatLngBounds }>;
}

const CapacitorGoogleMaps = registerPlugin<CapacitorGoogleMapsPlugin>(
  'CapacitorGoogleMaps',
  {
    web: () => import('./web').then(m => new m.CapacitorGoogleMapsWeb()),
  },
);

CapacitorGoogleMaps.addListener('isMapInFocus', data => {
  const x = data.x;
  const y = data.y;

  const elem = document.elementFromPoint(x, y) as HTMLElement | null;
  const internalId = elem?.dataset?.internalId;
  const mapInFocus = internalId === data.mapId;

  CapacitorGoogleMaps.dispatchMapEvent({ id: data.mapId, focus: mapInFocus });
});

export { CapacitorGoogleMaps };
