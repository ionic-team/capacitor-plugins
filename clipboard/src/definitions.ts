declare module '@capacitor/core' {
  interface PluginRegistry {
    Clipboard: ClipboardPlugin;
  }
}

export interface ClipboardPlugin {
  /**
   * Write a value to the clipboard (the "copy" action)
   */
  write(options: ClipboardWrite): Promise<void>;
  /**
   * Read a value from the clipboard (the "paste" action)
   */
  read(): Promise<ClipboardReadResult>;
}

export interface ClipboardWrite {
  string?: string;
  image?: string;
  url?: string;
  label?: string; // Android only
}

export interface ClipboardReadResult {
  value: string;
  type: string;
}
