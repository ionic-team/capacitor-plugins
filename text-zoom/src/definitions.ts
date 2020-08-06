declare module '@capacitor/core' {
  interface PluginRegistry {
    TextZoom: TextZoomPlugin;
  }
}

export interface TextZoomPlugin {
  /**
   * Get the current zoom level.
   *
   * Zoom levels are represented as a decimal (e.g. 1.2 is 120%).
   *
   * @since 0.0.1
   */
  get(): Promise<{ value: number }>;

  /**
   * Get the preferred zoom level.
   *
   * Zoom levels are represented as a decimal (e.g. 1.2 is 120%).
   *
   * @since 0.0.1
   */
  getPreferred(): Promise<{ value: number }>;

  /**
   * Set the current zoom level.
   *
   * Zoom levels are represented as a decimal (e.g. 1.2 is 120%).
   *
   * @since 0.0.1
   */
  set(options: { value: number }): Promise<void>;
}
