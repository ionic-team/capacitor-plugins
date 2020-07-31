import { WebPlugin } from '@capacitor/core';
import {
  HapticsPlugin,
  HapticsImpactOptions,
  HapticsNotificationOptions,
  VibrateOptions,
} from './definitions';

export class HapticsWeb extends WebPlugin implements HapticsPlugin {
  constructor() {
    super({ name: 'Haptics' });
  }

  async impact(options: HapticsImpactOptions): Promise<void> {
    console.log('impact options', options);
    return Promise.resolve();
  }

  async notification(options: HapticsNotificationOptions): Promise<void> {
    console.log('notification options', options);
    return Promise.resolve();
  }

  async vibrate(options?: VibrateOptions): Promise<void> {
    if (navigator.vibrate) {
      const duration = options?.duration || 300;
      navigator.vibrate(duration);
      return Promise.resolve();
    } else {
      return Promise.reject('Vibrate API not available in this browser')
    }
  }

  async selectionStart(): Promise<void> {
    console.log('selectionStart');
    return Promise.resolve();
  }

  async selectionChanged(): Promise<void> {
    console.log('selectionChanged');
    return Promise.resolve();
  }

  async selectionEnd(): Promise<void> {
    console.log('selectionEnd');
    return Promise.resolve();
  }
}
