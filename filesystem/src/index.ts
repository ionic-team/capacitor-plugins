import { registerPlugin } from '@capacitor/core';

import type { FilesystemPlugin } from './definitions';
import { FilesystemDirectory, FilesystemEncoding } from './definitions';

const Filesystem = registerPlugin<FilesystemPlugin>('Filesystem', {
  web: () => import('./web').then(m => new m.FilesystemWeb()),
});

export { Filesystem, FilesystemDirectory, FilesystemEncoding };
