declare module '@capacitor/core' {
  interface PluginRegistry {
    Clipboard: ClipboardPlugin;
  }
}

export interface ClipboardPlugin {
  /**
   * Write a value to the clipboard (the "copy" action)
   *
   * @since 1.0.0
   */
  write(options: ClipboardWrite): Promise<void>;

  /**
   * Read a value from the clipboard (the "paste" action)
   *
   * @since 1.0.0
   */
  read(): Promise<ClipboardReadResult>;
}

/**
 * Represents the data to be written to the clipboard.
 *
 * @since 1.0.0
 */
export interface ClipboardWrite {
  string?: string;
  image?: string;
  url?: string;
  label?: string; // Android only
}

/**
 * Represents the data read from the clipboard.
 *
 * @since 1.0.0
 */
export interface ClipboardReadResult {
  value: string;
  type: string;
}
