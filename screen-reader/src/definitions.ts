import { PluginListenerHandle } from '@capacitor/core';

declare module '@capacitor/core' {
  interface PluginRegistry {
    ScreenReader: ScreenReaderPlugin;
  }
}

export interface ScreenReaderSpeakOptions {
  /**
   * The text to speak.
   */
  value: string;

  /**
   * The language to speak the text in, as its [ISO 639-1 Code](https://www.loc.gov/standards/iso639-2/php/code_list.php) (ex: "en").
   * Currently only supported on Android.
   */
  language?: string;
}

export interface ScreenReaderPlugin {
  isEnabled(): Promise<{ value: boolean }>;
  speak(options: ScreenReaderSpeakOptions): Promise<void>;

  addListener(
    eventName: 'accessibilityScreenReaderStateChange',
    listener: any,
  ): PluginListenerHandle;

  removeAllListeners(): void;
}
