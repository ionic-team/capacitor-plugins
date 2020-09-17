import {
  Plugins,
  PluginImplementations,
  registerPlugin,
} from '@capacitor/core';

import { DialogPlugin } from './definitions';
import { DialogWeb } from './web';

const implementations: PluginImplementations<DialogPlugin> = {
  android: Plugins.Dialog,
  ios: Plugins.Dialog,
  web: new DialogWeb(),
};

const Dialog = registerPlugin('Dialog', implementations).getImplementation();

export { Dialog };
