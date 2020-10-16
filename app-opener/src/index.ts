import type { PluginImplementations } from '@capacitor/core';
import { Plugins, registerPlugin } from '@capacitor/core';

import type { AppOpenerPlugin } from './definitions';
import { AppOpenerWeb } from './web';

const implementations: PluginImplementations<AppOpenerPlugin> = {
  android: Plugins.AppOpener,
  ios: Plugins.AppOpener,
  web: new AppOpenerWeb(),
};

const AppOpener = registerPlugin(
  'AppOpener',
  implementations,
).getImplementation();

export { AppOpener };
