import type { PluginImplementations } from '@capacitor/core';
import { Plugins, registerPlugin } from '@capacitor/core';

import type { ToastPlugin } from './definitions';
import { ToastWeb } from './web';

const implementations: PluginImplementations<ToastPlugin> = {
  android: Plugins.Toast,
  ios: Plugins.Toast,
  web: new ToastWeb(),
};

const Toast = registerPlugin('Toast', implementations).getImplementation();

export { Toast };
