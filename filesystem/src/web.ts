import { WebPlugin } from '@capacitor/core';
import { FilesystemPlugin } from './definitions';

export class FilesystemWeb extends WebPlugin implements FilesystemPlugin {
  constructor() {
    super({ name: 'Filesystem' });
  }

  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
