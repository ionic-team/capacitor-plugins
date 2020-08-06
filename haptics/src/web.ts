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
  }

  async notification(options: HapticsNotificationOptions): Promise<void> {
    console.log('notification options', options);
  }

  async vibrate(options?: VibrateOptions): Promise<void> {
    if (navigator.vibrate) {
      const duration = options?.duration || 300;
      navigator.vibrate(duration);
    } else {
      return Promise.reject('Vibrate API not available in this browser');
    }
  }

  async selectionStart(): Promise<void> {
    console.log('selectionStart');
  }

  async selectionChanged(): Promise<void> {
    console.log('selectionChanged');
  }

  async selectionEnd(): Promise<void> {
    console.log('selectionEnd');
  }
}
