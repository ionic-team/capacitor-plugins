import type { PluginImplementations } from '@capacitor/core';
import { Plugins, registerPlugin } from '@capacitor/core';

import type { AppPlugin } from './definitions';
import { AppWeb } from './web';

const implementations: PluginImplementations<AppPlugin> = {
  android: Plugins.App,
  ios: Plugins.App,
  web: new AppWeb(),
};

const App = registerPlugin('App', implementations).getImplementation();

export { App };
