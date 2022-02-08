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

export interface AddMarkerArgs {
  id: string;
  marker: Marker;
}

export interface CapacitorGoogleMapsPlugin {
  create(args: CreateMapArgs): Promise<void>;
  addMarker(args: AddMarkerArgs): Promise<{ id: string }>;
  removeMarker(args: RemoveMarkerArgs): Promise<void>;
  destroy(args: DestroyMapArgs): Promise<void>;
}

export const CapacitorGoogleMaps = registerPlugin<CapacitorGoogleMapsPlugin>(
  'CapacitorGoogleMaps',
  {
    web: () => import('./web').then(m => new m.CapacitorGoogleMapsWeb()),
  },
);
