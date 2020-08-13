import { WebPlugin } from '@capacitor/core';
import { StoragePlugin } from './definitions';

export class StorageWeb extends WebPlugin implements StoragePlugin {
  constructor() {
    super({ name: 'Storage' });
  }

  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
