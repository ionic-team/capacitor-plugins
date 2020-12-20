import { registerPlugin } from '@capacitor/core';

import type { StoragePlugin } from './definitions';

const Storage = registerPlugin<StoragePlugin>('Storage', {
  web: () => import('./web').then(m => new m.StorageWeb()),
});

export * from './definitions';
export { Storage };
