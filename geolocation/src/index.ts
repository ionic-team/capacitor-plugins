import { registerPlugin } from '@capacitor/core';

import type { GeolocationPlugin } from './definitions';

const Geolocation = registerPlugin<GeolocationPlugin>('Geolocation', {
  web: () => import('./web').then(m => new m.GeolocationWeb()),
});

export * from './definitions';
export { Geolocation };
