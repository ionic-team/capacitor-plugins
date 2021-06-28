import { registerPlugin } from '@capacitor/core';

import type { KeyboardPlugin } from './definitions';

const Keyboard = registerPlugin<KeyboardPlugin>('Keyboard', {
    web: () => import('./web').then(m => new m.KeyboardWeb())
});

export * from './definitions';
export { Keyboard };
