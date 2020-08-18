import {
  Plugins,
  PluginImplementations,
  registerPlugin,
} from '@capacitor/core';

import { BrowserPlugin } from './definitions';
import { BrowserWeb } from './web';

const implementations: PluginImplementations<BrowserPlugin> = {
  android: Plugins.Browser,
  ios: Plugins.Browser,
  web: new BrowserWeb(),
};

const Browser = registerPlugin(
  'Browser',
  implementations,
).getImplementation();

export { Browser };
