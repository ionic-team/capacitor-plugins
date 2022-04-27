import { WebPlugin } from '@capacitor/core';

import type {
  HideOptions,
  ShowOptions,
  SplashScreenPlugin,
  UpdateProgressOptions,
} from './definitions';

export class SplashScreenWeb extends WebPlugin implements SplashScreenPlugin {
  async show(_options?: ShowOptions): Promise<void> {
    return undefined;
  }

  // Show and update progress of progress bar.
  async updateProgress(_options?: UpdateProgressOptions): Promise<void> {
    return undefined;
  }

  async hide(_options?: HideOptions): Promise<void> {
    return undefined;
  }
}
