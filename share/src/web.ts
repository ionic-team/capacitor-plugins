import { WebPlugin } from '@capacitor/core';
import { SharePlugin } from './definitions';

export class ShareWeb extends WebPlugin implements SharePlugin {
  constructor() {
    super({ name: 'Share' });
  }

  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
