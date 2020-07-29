import { WebPlugin } from '@capacitor/core';
import { ScreenReaderPlugin, ScreenReaderSpeakOptions } from './definitions';

export class ScreenReaderWeb extends WebPlugin implements ScreenReaderPlugin {
  constructor() {
    super({ name: 'ScreenReader' });
  }

  async isEnabled(): Promise<never> {
    throw new Error('This feature is not available in the browser.');
  }

  async speak(options: ScreenReaderSpeakOptions): Promise<void> {
    if (!('speechSynthesis' in window)) {
      throw new Error('Browser does not support the SpeechSynthesis API');
    }

    const utterance = new SpeechSynthesisUtterance(options.value);

    if (options.language) {
      utterance.lang = options.language;
    }

    speechSynthesis.speak(utterance);
  }
}
