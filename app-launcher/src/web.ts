import { WebPlugin } from '@capacitor/core';

import type { AppLauncherPlugin } from './definitions';

export class AppLauncherWeb extends WebPlugin implements AppLauncherPlugin {
  async canOpenUrl(_options: { url: string }): Promise<{ value: boolean }> {
    return { value: true };
  }

  async openUrl(_options: { url: string }): Promise<{ completed: boolean }> {
    return { completed: true };
  }
}
