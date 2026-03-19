import { registerPlugin } from '@capacitor/core';

import type { LocalNotificationsPlugin } from './definitions';

const LocalNotifications = registerPlugin<LocalNotificationsPlugin>('LocalNotifications', {
  web: () => import('./web').then((m) => new m.LocalNotificationsWeb()),
});

export * from './definitions';
export { LocalNotifications };
