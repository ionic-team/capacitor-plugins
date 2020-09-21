import type { PluginImplementations } from '@capacitor/core';
import { Plugins, registerPlugin } from '@capacitor/core';

import type { ActionSheetPlugin } from './definitions';
import { ActionSheetOptionStyle } from './definitions';
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
