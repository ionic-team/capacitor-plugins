import {
  Plugins,
  PluginImplementations,
  registerPlugin,
} from '@capacitor/core';

import { TextZoomPlugin } from './definitions';
import { TextZoomWeb } from './web';
import { TextZoomIOS } from './ios';

const implementations: PluginImplementations<TextZoomPlugin> = {
  android: Plugins.TextZoom,
  ios: new TextZoomIOS(),
  web: new TextZoomWeb(),
};

const TextZoom = registerPlugin(
  'TextZoom',
  implementations,
).getImplementation();

export { TextZoom };
