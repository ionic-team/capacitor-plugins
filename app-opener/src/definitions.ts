declare module '@capacitor/core' {
  interface PluginRegistry {
    AppOpener: AppOpenerPlugin;
  }
}

export interface AppOpenerPlugin {
  /**
   * Check if an app can be opened with the given URL.
   *
   * @since 1.0.0
   */
  canOpenUrl(options: { url: string }): Promise<{ value: boolean }>;

  /**
   * Open an app with the given URL.
   *
   * @since 1.0.0
   */
  openUrl(options: { url: string }): Promise<{ completed: boolean }>;
}
