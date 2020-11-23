import type { PluginImplementations } from '@capacitor/core';
import { registerPlugin } from '@capacitor/core';

import type { MotionPlugin } from './definitions';
import { MotionWeb } from './web';

const plugin = new MotionWeb();

const implementations: PluginImplementations<MotionPlugin> = {
  android: plugin,
  ios: plugin,
  web: plugin,
};

const Motion = registerPlugin('Motion', implementations).getImplementation();

export { Motion };
