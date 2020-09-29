import { WebPlugin } from '@capacitor/core';

import type { ToastPlugin, ToastShowOptions } from './definitions';

export class ToastWeb extends WebPlugin implements ToastPlugin {
  constructor() {
    super({ name: 'Toast' });
  }

  async show(options: ToastShowOptions): Promise<void> {
    let duration = 2000;
    if (options.duration) {
      duration = options.duration === 'long' ? 3500 : 2000;
    }
    const toast = document.createElement('pwa-toast') as any;
    toast.duration = duration;
    toast.message = options.text;
    document.body.appendChild(toast);
  }
}
