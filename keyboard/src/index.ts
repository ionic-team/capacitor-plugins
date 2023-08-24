import { registerPlugin } from '@capacitor/core';

import type { KeyboardPlugin } from './definitions';

const Keyboard = registerPlugin<KeyboardPlugin>('Keyboard');

export * from './definitions';
export { Keyboard };
