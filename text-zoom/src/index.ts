import { registerPlugin } from '@capacitor/core';

import type { TextZoomPlugin } from './definitions';

const TextZoom = registerPlugin<TextZoomPlugin>('TextZoom', {
  ios: () => import('./ios').then((m) => new m.TextZoomIOS()),
});

export * from './definitions';
export { TextZoom };
