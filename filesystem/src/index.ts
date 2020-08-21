import {
  Plugins,
  PluginImplementations,
  registerPlugin,
} from '@capacitor/core';

import { FilesystemPlugin } from './definitions';
import { FilesystemWeb } from './web';

const implementations: PluginImplementations<FilesystemPlugin> = {
  android: Plugins.Filesystem,
  ios: Plugins.Filesystem,
  web: new FilesystemWeb(),
};

const Filesystem = registerPlugin(
  'Filesystem',
  implementations,
).getImplementation();

export { Filesystem };
