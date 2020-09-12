import type { PluginImplementations } from '@capacitor/core';
import { Plugins, registerPlugin } from '@capacitor/core';

import type { StoragePlugin } from './definitions';
import { StorageWeb } from './web';

const implementations: PluginImplementations<StoragePlugin> = {
  android: Plugins.Storage,
  ios: Plugins.Storage,
  web: new StorageWeb(),
};

const Storage = registerPlugin('Storage', implementations).getImplementation();

export { Storage };
