import { registerPlugin } from '@capacitor/core';

import type { FilesystemPlugin } from './definitions';

const Filesystem = registerPlugin<FilesystemPlugin>('Filesystem', {
  web: () => import('./web').then(m => new m.FilesystemWeb()),
  electron: () => (window as any).CapacitorCustomPlatform.plugins.Filesystem,
});

export * from './definitions';
export { Filesystem };
