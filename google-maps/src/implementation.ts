import { registerPlugin } from '@capacitor/core';
import { GoogleMapConfig } from './map';

interface BaseMapArgs {
  id: string;
}
export interface CreateMapArgs extends BaseMapArgs {
  config: GoogleMapConfig;
  forceCreate?: boolean;
}

export interface DestroyMapArgs extends BaseMapArgs {}

export interface CapacitorGoogleMapsPlugin {
  create(args: CreateMapArgs): Promise<void>;
  destroy(args: DestroyMapArgs): Promise<void>;
}

export const CapacitorGoogleMaps = registerPlugin<CapacitorGoogleMapsPlugin>(
  'CapacitorGoogleMaps',
  {
    web: () => import('./web').then(m => new m.CapacitorGoogleMapsWeb()),
  },
);
