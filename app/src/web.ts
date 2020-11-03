import { WebPlugin } from '@capacitor/core';

import type { AppInfo, AppPlugin, AppLaunchUrl, AppState } from './definitions';

export class AppWeb extends WebPlugin implements AppPlugin {
  constructor() {
    super({ name: 'App' });
    if (typeof document !== 'undefined') {
      document.addEventListener(
        'visibilitychange',
        this.handleVisibilityChange.bind(this),
        false,
      );
    }
  }

  exitApp(): never {
    throw this.unimplemented('Not implemented on web.');
  }

  async getInfo(): Promise<AppInfo> {
    throw this.unimplemented('Not implemented on web.');
  }

  async getLaunchUrl(): Promise<AppLaunchUrl> {
    return { url: '' };
  }

  async getState(): Promise<AppState> {
    return { isActive: document.hidden !== true };
  }

  handleVisibilityChange(): void {
    const data = {
      isActive: document.hidden !== true,
    };

    this.notifyListeners('appStateChange', data);
  }
}
