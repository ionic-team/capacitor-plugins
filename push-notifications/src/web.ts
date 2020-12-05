import { WebPlugin } from '@capacitor/core';

import type { PushNotificationsPlugin } from './definitions';

export class PushNotificationsWeb
  extends WebPlugin
  implements PushNotificationsPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
