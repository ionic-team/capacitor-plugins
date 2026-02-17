import { WebPlugin } from '@capacitor/core';

import type { ToastPlugin, ShowOptions } from './definitions';

export class ToastWeb extends WebPlugin implements ToastPlugin {
  async show(options: ShowOptions): Promise<void> {
    if (typeof document !== 'undefined') {
      let duration = 2000;
      if (options.durationMilliseconds) {
        duration = options.durationMilliseconds;
      } else if (options.duration) {
        duration = options.duration === 'long' ? 3500 : 2000;
      }
      const toast = document.createElement('pwa-toast') as any;
      toast.duration = duration;
      toast.message = options.text;
      document.body.appendChild(toast);
    }
  }
}
