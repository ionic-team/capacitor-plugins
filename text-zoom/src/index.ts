import type { PluginImplementations } from '@capacitor/core';
import { Plugins, registerPlugin } from '@capacitor/core';

import type { TextZoomPlugin } from './definitions';
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
