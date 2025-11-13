import { WebPlugin } from '@capacitor/core';

import type { CanShareResult, ShareOptions, SharePlugin, ShareResult } from './definitions';

export class ShareWeb extends WebPlugin implements SharePlugin {
  async canShare(): Promise<CanShareResult> {
    if (typeof navigator === 'undefined' || !navigator.share) {
      return { value: false };
    } else {
      return { value: true };
    }
  }
  async share(options: ShareOptions): Promise<ShareResult> {
    if (typeof navigator === 'undefined' || !navigator.share) {
      throw this.unavailable('Share API not available in this browser');
    }

    await navigator.share({
      title: options.title,
      text: options.text,
      url: options.url,
    });
    return {};
  }
}
