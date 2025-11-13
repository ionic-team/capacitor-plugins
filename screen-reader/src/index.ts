import { registerPlugin } from '@capacitor/core';

import type { ScreenReaderPlugin } from './definitions';

const ScreenReader = registerPlugin<ScreenReaderPlugin>('ScreenReader', {
  web: () => import('./web').then((m) => new m.ScreenReaderWeb()),
});

export * from './definitions';
export { ScreenReader };
