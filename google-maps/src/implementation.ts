import { registerPlugin } from '@capacitor/core';

import { MapOptions } from './map';

export interface CapacitorGoogleMapsPlugin {
  create(id: string, options: MapOptions): Promise<void>;
  destroy(id: string): Promise<void>;
}

export const CapacitorGoogleMaps = registerPlugin<CapacitorGoogleMapsPlugin>(
  'CapacitorGoogleMaps',
  {
    web: () => import('./web').then(m => new m.CapacitorGoogleMapsWeb()),
  },
);
