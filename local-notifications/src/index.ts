import type { PluginImplementations } from '@capacitor/core';
import { Plugins, registerPlugin } from '@capacitor/core';

import type { LocalNotificationsPlugin } from './definitions';
import { LocalNotificationsWeb } from './web';

const implementations: PluginImplementations<LocalNotificationsPlugin> = {
  android: Plugins.LocalNotifications,
  ios: Plugins.LocalNotifications,
  web: new LocalNotificationsWeb(),
};

const LocalNotifications = registerPlugin(
  'LocalNotifications',
  implementations,
).getImplementation();

export { LocalNotifications };
