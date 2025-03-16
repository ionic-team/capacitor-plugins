import { WebPlugin } from '@capacitor/core';

import { ImpactStyle, NotificationType } from './definitions';
import type {
  HapticsPlugin,
  ImpactOptions,
  NotificationOptions,
  VibrateOptions,
} from './definitions';

export class HapticsWeb extends WebPlugin implements HapticsPlugin {
  selectionStarted = false;

  async impact(options?: ImpactOptions): Promise<void> {
    const pattern = this.patternForImpact(options?.style);
    this.vibrateWithPattern(pattern);
  }

  async notification(options?: NotificationOptions): Promise<void> {
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

  private patternForImpact(style: ImpactStyle = ImpactStyle.Heavy): number[] {
    if (style === ImpactStyle.Medium) {
      return [43];
    } else if (style === ImpactStyle.Light) {
      return [20];
    }
    return [61];
  }

  private patternForNotification(
    type: NotificationType = NotificationType.Success,
  ): number[] {
    if (type === NotificationType.Warning) {
      return [30, 40, 30, 50, 60];
    } else if (type === NotificationType.Error) {
      return [27, 45, 50];
    }
    return [35, 65, 21];
  }

  private vibrateWithPattern(pattern: number[]) {
    if (navigator.vibrate) {
      navigator.vibrate(pattern);
    } else {
      console.error('Browser does not support the vibrate API');
    }
  }
}
