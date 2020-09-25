import type { PluginImplementations } from '@capacitor/core';
import { Plugins, registerPlugin } from '@capacitor/core';

import type { DevicePlugin } from './definitions';
import { DeviceWeb } from './web';

const implementations: PluginImplementations<DevicePlugin> = {
  android: Plugins.Device,
  ios: Plugins.Device,
  web: new DeviceWeb(),
};

const Device = registerPlugin('Device', implementations).getImplementation();

export { Device };
