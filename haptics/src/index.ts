import {
  Plugins,
  PluginImplementations,
  registerPlugin,
} from '@capacitor/core';

import {
  HapticsPlugin,
  HapticsImpactStyle,
  HapticsNotificationType,
} from './definitions';
import { HapticsWeb } from './web';

const implementations: PluginImplementations<HapticsPlugin> = {
  android: Plugins.Haptics,
  ios: Plugins.Haptics,
  web: new HapticsWeb(),
};

const Haptics = registerPlugin('Haptics', implementations).getImplementation();

export { Haptics, HapticsImpactStyle, HapticsNotificationType };
