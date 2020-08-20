import {
  Plugins,
  PluginImplementations,
  registerPlugin,
} from '@capacitor/core';

import { ScreenReaderPlugin } from './definitions';
import { ScreenReaderWeb } from './web';

const implementations: PluginImplementations<ScreenReaderPlugin> = {
  android: Plugins.ScreenReader,
  ios: Plugins.ScreenReader,
  web: new ScreenReaderWeb(),
};

const ScreenReader = registerPlugin(
  'ScreenReader',
  implementations,
).getImplementation();

export { ScreenReader };
