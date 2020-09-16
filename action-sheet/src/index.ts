import {
  Plugins,
  PluginImplementations,
  registerPlugin,
} from '@capacitor/core';

import { ActionSheetOptionStyle, ActionSheetPlugin } from './definitions';
import { ActionSheetWeb } from './web';

const implementations: PluginImplementations<ActionSheetPlugin> = {
  android: Plugins.ActionSheet,
  ios: Plugins.ActionSheet,
  web: new ActionSheetWeb(),
};

const ActionSheet = registerPlugin(
  'ActionSheet',
  implementations,
).getImplementation();

export { ActionSheet, ActionSheetOptionStyle };
