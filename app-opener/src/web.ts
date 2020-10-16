import { WebPlugin } from '@capacitor/core';

import type { AppOpenerPlugin } from './definitions';

export class AppOpenerWeb extends WebPlugin implements AppOpenerPlugin {
  constructor() {
    super({ name: 'AppOpener' });
  }

  async canOpenUrl(_options: { url: string }): Promise<{ value: boolean }> {
    return { value: true };
  }

  async openUrl(_options: { url: string }): Promise<{ completed: boolean }> {
    return { completed: true };
  }
}
