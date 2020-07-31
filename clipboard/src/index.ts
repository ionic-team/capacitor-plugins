import {
  Plugins,
  PluginImplementations,
  registerPlugin,
} from '@capacitor/core';

import { ClipboardPlugin } from './definitions';
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
