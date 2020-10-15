import {
  Plugins,
  PluginImplementations,
  registerPlugin,
} from '@capacitor/core';

import { GeolocationPlugin, GeolocationPermissionStatus } from './definitions';
import { GeolocationWeb } from './web';

const implementations: PluginImplementations<GeolocationPlugin> = {
  android: Plugins.Geolocation,
  ios: Plugins.Geolocation,
  web: new GeolocationWeb(),
};

const Geolocation = registerPlugin(
  'Geolocation',
  implementations,
).getImplementation();

export { Geolocation, GeolocationPermissionStatus };
