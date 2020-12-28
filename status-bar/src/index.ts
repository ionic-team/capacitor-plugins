import { registerPlugin } from '@capacitor/core';

import type { StatusBarPlugin } from './definitions';

const StatusBar = registerPlugin<StatusBarPlugin>('StatusBar');

export * from './definitions';
export { StatusBar };
