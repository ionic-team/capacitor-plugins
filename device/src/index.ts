import { registerPlugin } from '@capacitor/core';

import type { DevicePlugin } from './definitions';

const Device = registerPlugin<DevicePlugin>('Device', {
  web: import('./web').then(m => new m.DeviceWeb()),
});

export { Device };
