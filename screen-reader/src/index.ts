import type { PluginImplementations } from '@capacitor/core';
import { Plugins, registerPlugin } from '@capacitor/core';

import type { ScreenReaderPlugin } from './definitions';
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
