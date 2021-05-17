import { registerPlugin } from '@capacitor/core';

import type { ClipboardPlugin } from './definitions';
import { ClipboardWeb } from './web';

const Clipboard = registerPlugin<ClipboardPlugin>('Clipboard', {
  web: () => new ClipboardWeb(),
});

export * from './definitions';
export { Clipboard };
