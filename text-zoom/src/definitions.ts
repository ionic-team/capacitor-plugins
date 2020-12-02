export interface GetResponse {
  /**
   * The current zoom level (represented as a decimal).
   *
   * @since 1.0.0
   */
  value: number;
}

export interface GetPreferredResponse {
  /**
   * The preferred zoom level (represented as a decimal).
   *
   * @since 1.0.0
   */
  value: number;
}

export interface SetOptions {
  /**
   * The new zoom level (represented as a decimal).
   *
   * @since 1.0.0
   */
  value: number;
}

export interface TextZoomPlugin {
  /**
   * Get the current zoom level.
   *
   * Zoom levels are represented as a decimal (e.g. 1.2 is 120%).
   *
   * @since 1.0.0
   */
  get(): Promise<GetResponse>;

  /**
   * Get the preferred zoom level.
   *
   * Zoom levels are represented as a decimal (e.g. 1.2 is 120%).
   *
   * @since 1.0.0
   */
  getPreferred(): Promise<GetPreferredResponse>;

  /**
   * Set the current zoom level.
   *
   * Zoom levels are represented as a decimal (e.g. 1.2 is 120%).
   *
   * @since 1.0.0
   */
  set(options: SetOptions): Promise<void>;
}
