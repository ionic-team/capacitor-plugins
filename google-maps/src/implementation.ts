import type { PluginListenerHandle } from '@capacitor/core';
import { registerPlugin } from '@capacitor/core';

import type { GoogleMapConfig } from './definitions';
import type { Marker } from './map';

export interface CreateMapArgs {
  id: string;
  apiKey: string;
  config: GoogleMapConfig;
  forceCreate?: boolean;
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

export interface AddMarkersArgs {
  id: string;
  markers: Marker[];
}

export type MapListenerCallback = (data: any) => void;

export interface OnMapClickArgs {
  callback?: MapListenerCallback;
}

export interface OnMarkerClickArgs {
  callback?: MapListenerCallback;
}

export interface CapacitorGoogleMapsPlugin {
  create(args: CreateMapArgs): Promise<void>;
  addMarker(args: AddMarkerArgs): Promise<{ id: string }>;
  addMarkers(args: AddMarkersArgs): Promise<{ ids: string[] }>;
  removeMarker(args: RemoveMarkerArgs): Promise<void>;
  removeMarkers(args: RemoveMarkersArgs): Promise<void>;
  enableClustering(args: { id: string }): Promise<void>;
  disableClustering(args: { id: string }): Promise<void>;
  destroy(args: DestroyMapArgs): Promise<void>;
  setOnMapClickListener(args: OnMapClickArgs): Promise<void>;
  setOnMarkerClickListener(args: OnMarkerClickArgs): Promise<void>;

  addListener(
    eventName: 'onMapReady',
    listenerFunc: MapListenerCallback,
  ): PluginListenerHandle;

  addListener(
    eventName: 'onMapClick',
    listenerFunc: MapListenerCallback,
  ): PluginListenerHandle;

  addListener(
    eventName: 'onMarkerClick',
    listenerFunc: MapListenerCallback,
  ): PluginListenerHandle;
}

export const CapacitorGoogleMaps = registerPlugin<CapacitorGoogleMapsPlugin>(
  'CapacitorGoogleMaps',
  {
    web: () => import('./web').then(m => new m.CapacitorGoogleMapsWeb()),
  },
);
