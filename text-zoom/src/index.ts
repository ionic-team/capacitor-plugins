import { registerPlugin } from '@capacitor/core';

import type { TextZoomPlugin } from './definitions';

const TextZoom = registerPlugin<TextZoomPlugin>('TextZoom', {
  web: () => import('./web').then(m => new m.TextZoomWeb()),
});

export * from './definitions';
export { TextZoom };
