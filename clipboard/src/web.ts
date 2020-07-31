import { WebPlugin } from '@capacitor/core';
import { ClipboardPlugin } from './definitions';

export class ClipboardWeb extends WebPlugin implements ClipboardPlugin {
  constructor() {
    super({
      name: 'Clipboard',
      platforms: ['web'],
    });
  }

  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}

const Clipboard = new ClipboardWeb();

export { Clipboard };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(Clipboard);
