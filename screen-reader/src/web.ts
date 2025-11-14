import { WebPlugin } from '@capacitor/core';

import type { ScreenReaderPlugin, SpeakOptions } from './definitions';

export class ScreenReaderWeb extends WebPlugin implements ScreenReaderPlugin {
  async isEnabled(): Promise<never> {
    throw this.unavailable('This feature is not available in the browser.');
  }

  async speak(options: SpeakOptions): Promise<void> {
    if (!('speechSynthesis' in window)) {
      throw this.unavailable('Browser does not support the SpeechSynthesis API');
    }

    const utterance = new SpeechSynthesisUtterance(options.value);

    if (options.language) {
      utterance.lang = options.language;
    }

    speechSynthesis.speak(utterance);
  }
}
