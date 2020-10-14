import type { PluginImplementations } from '@capacitor/core';
import { Plugins, registerPlugin } from '@capacitor/core';

import type { StatusBarPlugin } from './definitions';
import { StatusBarStyle } from './definitions';

const implementations: PluginImplementations<StatusBarPlugin> = {
  android: Plugins.StatusBar,
  ios: Plugins.StatusBar,
};

const StatusBar = registerPlugin(
  'StatusBar',
  implementations,
).getImplementation();

export { StatusBar, StatusBarStyle };
