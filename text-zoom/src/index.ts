import {
  Plugins,
  PluginImplementations,
  registerPlugin,
} from '@capacitor/core';

import { TextZoomPlugin } from './definitions';
import { TextZoomWeb } from './web';

const implementations: PluginImplementations<TextZoomPlugin> = {
  android: Plugins.TextZoom,
  ios: Plugins.TextZoom,
  web: new TextZoomWeb(),
};

const TextZoom = registerPlugin(
  'TextZoom',
  implementations,
).getImplementation();

export { TextZoom };
