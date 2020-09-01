import {
  Plugins,
  PluginImplementations,
  registerPlugin,
} from '@capacitor/core';

import { NetworkPlugin, NetworkStatus } from './definitions';
import { NetworkWeb } from './web';

const implementations: PluginImplementations<NetworkPlugin> = {
  android: Plugins.Network,
  ios: Plugins.Network,
  web: new NetworkWeb(),
};

const Network = registerPlugin('Network', implementations).getImplementation();

export { Network, NetworkStatus };
