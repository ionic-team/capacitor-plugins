import { registerPlugin } from '@capacitor/core';

import type { SharePlugin } from './definitions';

const Share = registerPlugin<SharePlugin>('Share', {
  web: () => import('./web').then((m) => new m.ShareWeb()),
});

export * from './definitions';
export { Share };
