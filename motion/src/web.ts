import { WebPlugin } from '@capacitor/core';

import type { MotionPlugin } from './definitions';

export class MotionWeb extends WebPlugin implements MotionPlugin {
  constructor() {
    super({ name: 'Motion' });
    this.registerWindowListener('devicemotion', 'accel');
    this.registerWindowListener('deviceorientation', 'orientation');
  }
}
