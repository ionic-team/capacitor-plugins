import { registerPlugin } from '@capacitor/core';

import type { CameraPlugin } from './definitions';
// See https://github.com/ionic-team/capacitor-plugins/issues/1314
import * as web from './web';

const Camera = registerPlugin<CameraPlugin>('Camera', {
  web: () => new web.CameraWeb(),
});

export * from './definitions';
export { Camera };
