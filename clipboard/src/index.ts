import { registerPlugin } from '@capacitor/core';

import { ClipboardWeb } from './web';

import type { ClipboardPlugin } from './definitions';

const Clipboard = registerPlugin<ClipboardPlugin>('Clipboard', {
  web: () => new ClipboardWeb(),
});

export * from './definitions';
export { Clipboard };
