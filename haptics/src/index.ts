import { registerPlugin } from '@capacitor/core';

import type { HapticsPlugin } from './definitions';
import { HapticsImpactStyle, HapticsNotificationType } from './definitions';

const Haptics = registerPlugin<HapticsPlugin>('Haptics', {
  web: () => import('./web').then(m => new m.HapticsWeb()),
});

export { Haptics, HapticsImpactStyle, HapticsNotificationType };
