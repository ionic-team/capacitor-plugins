import type { PluginImplementations } from '@capacitor/core';
import { Plugins, registerPlugin } from '@capacitor/core';

import type { NetworkPlugin } from './definitions';
import { NetworkStatus } from './definitions';
import { NetworkWeb } from './web';

const implementations: PluginImplementations<NetworkPlugin> = {
  android: Plugins.Network,
  ios: Plugins.Network,
  web: new NetworkWeb(),
};

const Network = registerPlugin('Network', implementations).getImplementation();

export { Network, NetworkStatus };
