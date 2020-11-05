import { WebPlugin } from '@capacitor/core';

import type { NotificationsPlugin } from './definitions';

export class NotificationsWeb extends WebPlugin implements NotificationsPlugin {
  constructor() {
    super({ name: 'Notifications' });
  }

  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
