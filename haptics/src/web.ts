import { WebPlugin } from '@capacitor/core';
import { HapticsPlugin } from './definitions';

export class HapticsWeb extends WebPlugin implements HapticsPlugin {
  constructor() {
    super({ name: 'Haptics' });
  }

  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
