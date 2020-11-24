import { registerPlugin } from '@capacitor/core';

import type { StatusBarPlugin } from './definitions';
import { StatusBarStyle } from './definitions';

const StatusBar = registerPlugin<StatusBarPlugin>('StatusBar');

export { StatusBar, StatusBarStyle };
