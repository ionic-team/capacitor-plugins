import type { PluginImplementations } from '@capacitor/core';
import { Plugins, registerPlugin } from '@capacitor/core';

import type { KeyboardPlugin } from './definitions';
import { KeyboardResize, KeyboardStyle } from './definitions';

const implementations: PluginImplementations<KeyboardPlugin> = {
  android: Plugins.Keyboard,
  ios: Plugins.Keyboard
};

const Keyboard = registerPlugin(
  'Keyboard',
  implementations,
).getImplementation();

export { Keyboard, KeyboardResize, KeyboardStyle };
