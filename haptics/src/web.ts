import { WebPlugin } from '@capacitor/core';

import type {
  HapticsPlugin,
  HapticsImpactOptions,
  HapticsNotificationOptions,
  VibrateOptions,
} from './definitions';
import { HapticsImpactStyle, HapticsNotificationType } from './definitions';

export class HapticsWeb extends WebPlugin implements HapticsPlugin {
  constructor() {
    super({ name: 'Haptics' });
  }

  selectionStarted = false;

  async impact(options?: HapticsImpactOptions): Promise<void> {
    const pattern = this.patternForImpact(options?.style);
    this.vibrateWithPattern(pattern);
  }

  async notification(options?: HapticsNotificationOptions): Promise<void> {
    const pattern = this.patternForNotification(options?.type);
    this.vibrateWithPattern(pattern);
  }

  async vibrate(options?: VibrateOptions): Promise<void> {
    const duration = options?.duration || 300;
    this.vibrateWithPattern([duration]);
  }

  async selectionStart(): Promise<void> {
    this.selectionStarted = true;
  }

  async selectionChanged(): Promise<void> {
    if (this.selectionStarted) {
      this.vibrateWithPattern([70]);
    }
  }

  async selectionEnd(): Promise<void> {
    this.selectionStarted = false;
  }

  private patternForImpact(
    style: HapticsImpactStyle = HapticsImpactStyle.Heavy,
  ): number[] {
    if (style === HapticsImpactStyle.Medium) {
      return [43];
    } else if (style === HapticsImpactStyle.Light) {
      return [20];
    }
    return [61];
  }

  private patternForNotification(
    type: HapticsNotificationType = HapticsNotificationType.Success,
  ): number[] {
    if (type === HapticsNotificationType.Warning) {
      return [30, 40, 30, 50, 60];
    } else if (type === HapticsNotificationType.Error) {
      return [27, 45, 50];
    }
    return [35, 65, 21];
  }

  private vibrateWithPattern(pattern: number[]) {
    if (navigator.vibrate) {
      navigator.vibrate(pattern);
    } else {
      throw this.unavailable('Browser does not support the vibrate API');
    }
  }
}
