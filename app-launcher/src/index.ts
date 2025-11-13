import { registerPlugin } from '@capacitor/core';

import type { AppLauncherPlugin } from './definitions';

const AppLauncher = registerPlugin<AppLauncherPlugin>('AppLauncher', {
  web: () => import('./web').then((m) => new m.AppLauncherWeb()),
});

export * from './definitions';
export { AppLauncher };
