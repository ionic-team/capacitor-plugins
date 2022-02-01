import { registerPlugin } from '@capacitor/core';

import type { GoogleMapConfig } from './definitions';

export interface CreateMapArgs {
  id: string;
  config: GoogleMapConfig;
  forceCreate?: boolean;
}

export interface DestroyMapArgs {
  id: string;
}

export interface InitializeMapArgs {
  key: string;
}

export interface CapacitorGoogleMapsPlugin {
  initialize(args: InitializeMapArgs): Promise<void>;
  create(args: CreateMapArgs): Promise<void>;
  destroy(args: DestroyMapArgs): Promise<void>;
}

export const CapacitorGoogleMaps = registerPlugin<CapacitorGoogleMapsPlugin>(
  'CapacitorGoogleMaps',
  {
    web: () => import('./web').then(m => new m.CapacitorGoogleMapsWeb()),
  },
);
