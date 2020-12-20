import { WebPlugin } from '@capacitor/core';

import type {
  AppLauncherPlugin,
  CanOpenURLOptions,
  CanOpenURLResult,
  OpenURLOptions,
  OpenURLResult,
} from './definitions';

export class AppLauncherWeb extends WebPlugin implements AppLauncherPlugin {
  async canOpenUrl(_options: CanOpenURLOptions): Promise<CanOpenURLResult> {
    return { value: true };
  }

  async openUrl(_options: OpenURLOptions): Promise<OpenURLResult> {
    return { completed: true };
  }
}
