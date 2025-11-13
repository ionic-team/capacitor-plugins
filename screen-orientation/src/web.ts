import { WebPlugin } from '@capacitor/core';

import type {
  OrientationLockOptions,
  OrientationLockType,
  ScreenOrientationPlugin,
  ScreenOrientationResult,
} from './definitions';

// Extend ScreenOrientation interface to include lock method
// TypeScript's DOM lib removed lock() but it still exists in the browser API
// See: https://github.com/microsoft/TypeScript-DOM-lib-generator/issues/1615
interface ScreenOrientationWithLock extends ScreenOrientation {
  lock(orientation: OrientationLockType): Promise<void>;
}

export class ScreenOrientationWeb
  extends WebPlugin
  implements ScreenOrientationPlugin
{
  constructor() {
    super();
    if (typeof screen !== 'undefined' && typeof screen.orientation !== 'undefined') {
      screen.orientation.addEventListener('change', () => {
        const type = screen.orientation.type;
        this.notifyListeners('screenOrientationChange', { type });
      });
    }
  }

  async orientation(): Promise<ScreenOrientationResult> {
    if (typeof screen === 'undefined' || !screen.orientation) {
      throw this.unavailable('ScreenOrientation API not available in this browser');
    }
    return { type: screen.orientation.type };
  }

  async lock(options: OrientationLockOptions): Promise<void> {
    if (
      typeof screen === 'undefined' ||
      !screen.orientation ||
      !(screen.orientation as ScreenOrientationWithLock).lock
    ) {
      throw this.unavailable(
        'ScreenOrientation API not available in this browser',
      );
    }
    try {
      await (screen.orientation as ScreenOrientationWithLock).lock(
        options.orientation,
      );
    } catch {
      throw this.unavailable('ScreenOrientation API not available in this browser');
    }
  }

  async unlock(): Promise<void> {
    if (typeof screen === 'undefined' || !screen.orientation || !screen.orientation.unlock) {
      throw this.unavailable('ScreenOrientation API not available in this browser');
    }
    try {
      screen.orientation.unlock();
    } catch {
      throw this.unavailable('ScreenOrientation API not available in this browser');
    }
  }
}
