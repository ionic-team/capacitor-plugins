import { WebPlugin } from '@capacitor/core';

import type {
  SplashScreenPlugin,
  SplashScreenShowOptions,
  SplashScreenHideOptions,
} from './definitions';

export class SplashScreenWeb extends WebPlugin implements SplashScreenPlugin {
  async show(_options?: SplashScreenShowOptions): Promise<void> {
    return undefined;
  }

  async hide(_options?: SplashScreenHideOptions): Promise<void> {
    return undefined;
  }
}
