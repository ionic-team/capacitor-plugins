import { registerPlugin } from '@capacitor/core';

import type { AppPlugin } from './definitions';

const App = registerPlugin<AppPlugin>('App', {
  web: () => import('./web').then(m => new m.AppWeb()),
});

export * from './definitions';
export { App };
