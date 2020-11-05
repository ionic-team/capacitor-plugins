import type { PluginImplementations } from '@capacitor/core';
import { Plugins, registerPlugin } from '@capacitor/core';

import type { NotificationsPlugin } from './definitions';
import { NotificationsWeb } from './web';

const implementations: PluginImplementations<NotificationsPlugin> = {
  android: Plugins.Notifications,
  ios: Plugins.Notifications,
  web: new NotificationsWeb(),
};

const Notifications = registerPlugin(
  'Notifications',
  implementations,
).getImplementation();

export { Notifications };
