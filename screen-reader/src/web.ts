import { WebPlugin } from '@capacitor/core';
import { ScreenReaderPlugin } from './definitions';

export class ScreenReaderWeb extends WebPlugin implements ScreenReaderPlugin {
  constructor() {
    super({ name: 'ScreenReader' });
  }

  async isEnabled(): Promise<{ value: boolean }> {
    return { value: false };
  }

  async speak(): Promise<void> {
    console.warn('unimplemented');
  }
}
