import type {
  CanOpenURLOptions,
  CanOpenURLResult,
  OpenURLOptions,
  OpenURLResult,
} from './../src/definitions';

export interface AppLauncherPlugin {
  /**
   * Check if an app can be opened with the given URL.
   *
   * This is not available in Electron and thus will always return true.
   *
   * @since 1.0.9
   */
  canOpenUrl(options: CanOpenURLOptions): Promise<CanOpenURLResult>;

  /**
   * Open an app with the given URL.
   *
   * @since 1.0.9
   */
  openUrl(options: OpenURLOptions): Promise<OpenURLResult>;
}
