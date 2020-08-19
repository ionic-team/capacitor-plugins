import { WebPlugin } from '@capacitor/core';
import { MotionPlugin } from './definitions';

export class MotionWeb extends WebPlugin implements MotionPlugin {
  constructor() {
    super({ name: 'Motion' });
  }

  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
