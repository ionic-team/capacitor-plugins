import {
  PluginImplementations,
  registerPlugin,
} from '@capacitor/core';

import { MotionPlugin } from './definitions';
import { MotionWeb } from './web';

const implementations: PluginImplementations<MotionPlugin> = {
  android: new MotionWeb(),
  ios: new MotionWeb(),
  web: new MotionWeb(),
};

const Motion = registerPlugin(
  'Motion',
  implementations,
).getImplementation();

export { Motion };
