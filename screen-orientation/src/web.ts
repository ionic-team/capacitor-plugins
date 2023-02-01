import { WebPlugin } from '@capacitor/core';

import type { OrientationLockOptions, ScreenOrientationPlugin, ScreenOrientationResult } from './definitions';

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

  async orientation(): Promise<ScreenOrientationResult> {
    return { type: window.screen.orientation.type };
  }

  async lock(options: OrientationLockOptions): Promise<void> {
    await window.screen.orientation.lock(options.orientation);
  }

  async unlock(): Promise<void> {
    window.screen.orientation.unlock();
  }
}
