import { WebPlugin } from '@capacitor/core';

import type { ScreenOrientationPlugin } from './definitions';

export class ScreenOrientationWeb
  extends WebPlugin
  implements ScreenOrientationPlugin
{
  constructor() {
    super();
    window.screen.orientation.addEventListener('change', () => {
      const type = window.screen.orientation.type;
      this.notifyListeners('screenOrientationChange', { type });
    });
  }

  async orientation(): Promise<{ type: OrientationType }> {
    return { type: window.screen.orientation.type };
  }

  async lock(opts: { orientation: OrientationLockType }): Promise<void> {
    await window.screen.orientation.lock(opts.orientation);
  }

  async unlock(): Promise<void> {
    window.screen.orientation.unlock();
  }
}
