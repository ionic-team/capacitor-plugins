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

  impact(options: HapticsImpactOptions): void {
    console.log('impact options', options);
  }

  notification(options: HapticsNotificationOptions): void {
    console.log('notification options', options);
  }

  vibrate(options?: VibrateOptions): void {
    const duration = options?.duration || 300;
    navigator.vibrate(duration);
  }

  selectionStart(): void {
    console.log('selectionStart');
  }

  selectionChanged(): void {
    console.log('selectionChanged');
  }

  selectionEnd(): void {
    console.log('selectionEnd');
  }
}
