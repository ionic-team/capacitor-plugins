import type { PluginListenerHandle } from '@capacitor/core';

export interface SpeakOptions {
  /**
   * The text to speak.
   *
   * @since 1.0.0
   */
  value: string;

  /**
   * The language to speak the text in, as its [ISO 639-1
   * Code](https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes) (e.g.: "en").
   *
   * This option is only supported on Android.
   *
   * @since 1.0.0
   */
  language?: string;
}

export interface ScreenReaderState {
  /**
   * Whether a Screen Reader is currently active.
   *
   * @since 1.0.0
   */
  value: boolean;
}

export type StateChangeListener = (state: ScreenReaderState) => void;

export interface ScreenReaderPlugin {
  /**
   * Whether a Screen Reader is currently active.
   *
   * This method is not supported on web (it is not possible to detect Screen
   * Readers).
   *
   * @since 1.0.0
   */
  isEnabled(): Promise<{ value: boolean }>;

  /**
   * Text-to-Speech functionality.
   *
   * This function will only work if a Screen Reader is currently active.
   *
   * On web, browsers must support the [SpeechSynthesis
   * API](https://developer.mozilla.org/en-US/docs/Web/API/SpeechSynthesis), or
   * this method will throw an error.
   *
   * For more text-to-speech capabilities, please see the [Capacitor Community
   * Text-to-Speech
   * plugin](https://github.com/capacitor-community/text-to-speech).
   *
   * @since 1.0.0
   */
  speak(options: SpeakOptions): Promise<void>;

  /**
   * Add a listener for when the screen reader is turned on or off.
   *
   * This event used to be named `'accessibilityScreenReaderStateChange'`.
   *
   * This method is not supported on web (it is not possible to detect Screen
   * Readers).
   *
   * @since 1.0.0
   */
  addListener(
    eventName: 'stateChange',
    listener: StateChangeListener,
  ): Promise<PluginListenerHandle>;

  /**
   * Remove all the listeners that are attached to this plugin.
   *
   * @since 1.0.0
   */
  removeAllListeners(): Promise<void>;
}

/**
 * @deprecated Use `SpeakOptions`.
 * @since 1.0.0
 */
export type AccessibilitySpeakOptions = SpeakOptions;

/**
 * @deprecated Use `StateChangeListener`.
 * @since 1.0.0
 */
export type ScreenReaderStateChangeCallback = StateChangeListener;
