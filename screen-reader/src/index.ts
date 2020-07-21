import { Plugins, registerPlugin } from '@capacitor/core';
import { ScreenReaderWeb } from './web';

const ScreenReader = registerPlugin('ScreenReader', {
  android: Plugins.ScreenReader,
  ios: Plugins.ScreenReader,
  web: new ScreenReaderWeb(),
}).getImplementation();

export { ScreenReader };
