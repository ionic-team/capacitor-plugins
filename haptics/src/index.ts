import { registerPlugin } from '@capacitor/core';

import type { HapticsPlugin } from './definitions';

const Haptics = registerPlugin<HapticsPlugin>('Haptics', {
  web: () => import('./web').then(m => new m.HapticsWeb()),
});

export * from './definitions';
export { Haptics };
