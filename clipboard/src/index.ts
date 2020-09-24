import type { PluginImplementations } from '@capacitor/core';
import { Plugins, registerPlugin } from '@capacitor/core';

import type { ClipboardPlugin } from './definitions';
import { ClipboardWeb } from './web';

const implementations: PluginImplementations<ClipboardPlugin> = {
  android: Plugins.Clipboard,
  ios: Plugins.Clipboard,
  web: new ClipboardWeb(),
};

const Clipboard = registerPlugin(
  'Clipboard',
  implementations,
).getImplementation();

export { Clipboard };
