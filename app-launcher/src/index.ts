import type { PluginImplementations } from '@capacitor/core';
import { Plugins, registerPlugin } from '@capacitor/core';

import type { AppLauncherPlugin } from './definitions';
import { AppLauncherWeb } from './web';

const implementations: PluginImplementations<AppLauncherPlugin> = {
  android: Plugins.AppLauncher,
  ios: Plugins.AppLauncher,
  web: new AppLauncherWeb(),
};

const AppLauncher = registerPlugin(
  'AppLauncher',
  implementations,
).getImplementation();

export { AppLauncher };
