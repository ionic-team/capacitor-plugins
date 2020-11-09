import { registerPlugin } from '@capacitor/core';

import type { NetworkPlugin } from './definitions';
import { NetworkStatus } from './definitions';

const Network = registerPlugin<NetworkPlugin>('Network', {
  web: import('./web').then(m => new m.NetworkWeb()),
});

export { Network, NetworkStatus };
