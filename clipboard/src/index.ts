import { registerPlugin } from '@capacitor/core';

import type { ClipboardPlugin } from './definitions';

const Clipboard = registerPlugin<ClipboardPlugin>('Clipboard', {
  web: () => import('./web').then(m => new m.ClipboardWeb()),
});

export * from './definitions';
export { Clipboard };
