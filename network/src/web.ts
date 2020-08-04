import { WebPlugin } from '@capacitor/core';
import { NetworkPlugin } from './definitions';

export class NetworkWeb extends WebPlugin implements NetworkPlugin {
  constructor() {
    super({ name: 'Network' });
  }

  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
