import { WebPlugin } from '@capacitor/core';

import type { ShareOptions, SharePlugin, ShareResult } from './definitions';

export class ShareWeb extends WebPlugin implements SharePlugin {
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
