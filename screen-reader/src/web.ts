import { WebPlugin } from '@capacitor/core';
import { ScreenReaderPlugin } from './definitions';

export class ScreenReaderWeb extends WebPlugin implements ScreenReaderPlugin {
  constructor() {
    super({ name: 'ScreenReader' });
  }

  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
