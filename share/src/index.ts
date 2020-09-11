import {
  Plugins,
  PluginImplementations,
  registerPlugin,
} from '@capacitor/core';

import { SharePlugin } from './definitions';
import { ShareWeb } from './web';

const implementations: PluginImplementations<SharePlugin> = {
  android: Plugins.Share,
  ios: Plugins.Share,
  web: new ShareWeb(),
};

const Share = registerPlugin('Share', implementations).getImplementation();

export { Share };
