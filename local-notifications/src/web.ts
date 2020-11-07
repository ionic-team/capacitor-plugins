import { WebPlugin } from '@capacitor/core';

import type { LocalNotificationsPlugin } from './definitions';

export class LocalNotificationsWeb
  extends WebPlugin
  implements LocalNotificationsPlugin {
  constructor() {
    super({ name: 'LocalNotifications' });
  }

  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
