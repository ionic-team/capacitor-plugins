import { UnsupportedBrowserException, WebPlugin } from '@capacitor/core';

import type {
  ScreenReaderPlugin,
  ScreenReaderSpeakOptions,
} from './definitions';

export class ScreenReaderWeb extends WebPlugin implements ScreenReaderPlugin {
  constructor() {
    super({ name: 'ScreenReader' });
  }

  async isEnabled(): Promise<never> {
    throw new UnsupportedBrowserException(
      'This feature is not available in the browser.',
    );
  }

  async speak(options: ScreenReaderSpeakOptions): Promise<void> {
    if (!('speechSynthesis' in window)) {
      throw new UnsupportedBrowserException(
        'Browser does not support the SpeechSynthesis API',
      );
    }

    const utterance = new SpeechSynthesisUtterance(options.value);

    if (options.language) {
      utterance.lang = options.language;
    }

    speechSynthesis.speak(utterance);
  }
}
