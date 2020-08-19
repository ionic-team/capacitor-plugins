import {
  Plugins,
  PluginImplementations,
  registerPlugin,
} from '@capacitor/core';

import { TextZoomPlugin } from './definitions';
import { TextZoomIOS } from './ios';

const implementations: PluginImplementations<TextZoomPlugin> = {
  android: Plugins.TextZoom,
  ios: new TextZoomIOS(),
};

const TextZoom = registerPlugin(
  'TextZoom',
  implementations,
).getImplementation();

export { TextZoom };
