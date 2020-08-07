declare module '@capacitor/core' {
  interface PluginRegistry {
    TextZoom: TextZoomPlugin;
  }
}

export interface GetResponse {
  /**
   * The current zoom level (represented as a decimal).
   *
   * @since 0.0.1
   */
  value: number;
}

export interface GetPreferredResponse {
  /**
   * The preferred zoom level (represented as a decimal).
   *
   * @since 0.0.1
   */
  value: number;
}

export interface SetOptions {
  /**
   * The new zoom level (represented as a decimal).
   *
   * @since 0.0.1
   */
  value: number;
}

export interface TextZoomPlugin {
  /**
   * Get the current zoom level.
   *
   * Zoom levels are represented as a decimal (e.g. 1.2 is 120%).
   *
   * @since 0.0.1
   */
  get(): Promise<GetResponse>;

  /**
   * Get the preferred zoom level.
   *
   * Zoom levels are represented as a decimal (e.g. 1.2 is 120%).
   *
   * @since 0.0.1
   */
  getPreferred(): Promise<GetPreferredResponse>;

  /**
   * Set the current zoom level.
   *
   * Zoom levels are represented as a decimal (e.g. 1.2 is 120%).
   *
   * @since 0.0.1
   */
  set(options: SetOptions): Promise<void>;
}
