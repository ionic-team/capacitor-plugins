import { WebPlugin } from '@capacitor/core';
import { ScreenReaderPlugin } from './definitions';

export class ScreenReaderWeb extends WebPlugin implements ScreenReaderPlugin {
  constructor() {
    super({
      name: 'ScreenReader',
      platforms: ['web']
    });
  }

  async echo(options: { value: string }): Promise<{value: string}> {
    console.log('ECHO', options);
    return options;
  }
}

const ScreenReader = new ScreenReaderWeb();

export { ScreenReader };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(ScreenReader);
