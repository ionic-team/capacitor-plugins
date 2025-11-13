import { registerPlugin } from '@capacitor/core';

import type { ToastPlugin } from './definitions';

const Toast = registerPlugin<ToastPlugin>('Toast', {
  web: () => import('./web').then((m) => new m.ToastWeb()),
});

export * from './definitions';
export { Toast };
