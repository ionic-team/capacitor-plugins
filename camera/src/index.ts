import { registerPlugin } from '@capacitor/core';

import type { CameraPlugin } from './definitions';
import {
  CameraOptions,
  CameraDirection,
  CameraPhoto,
  CameraResultType,
  CameraSource,
} from './definitions';

const Camera = registerPlugin<CameraPlugin>('Camera', {
  web: () => import('./web').then(m => new m.CameraWeb()),
});

export {
  Camera,
  CameraOptions,
  CameraDirection,
  CameraPhoto,
  CameraResultType,
  CameraSource,
};
