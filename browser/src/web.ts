import { WebPlugin } from '@capacitor/core';
import { BrowserPlugin } from './definitions';

export class BrowserWeb extends WebPlugin implements BrowserPlugin {
  constructor() {
    super({ name: 'Browser' });
  }

  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
