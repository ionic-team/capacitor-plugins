import type { PluginImplementations } from '@capacitor/core';
import { Plugins, registerPlugin } from '@capacitor/core';

import type { HapticsPlugin } from './definitions';
import { HapticsImpactStyle, HapticsNotificationType } from './definitions';
import { HapticsWeb } from './web';

const implementations: PluginImplementations<HapticsPlugin> = {
  android: Plugins.Haptics,
  ios: Plugins.Haptics,
  web: new HapticsWeb(),
};

const Haptics = registerPlugin('Haptics', implementations).getImplementation();

export { Haptics, HapticsImpactStyle, HapticsNotificationType };
