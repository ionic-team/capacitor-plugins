import { registerPlugin } from '@capacitor/core';

import type { KeyboardPlugin } from './definitions';
import { KeyboardResize, KeyboardStyle } from './definitions';

const Keyboard = registerPlugin<KeyboardPlugin>('Keyboard');

export { Keyboard, KeyboardResize, KeyboardStyle };
