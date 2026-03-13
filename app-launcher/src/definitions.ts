export interface AppLauncherPlugin {
  /**
   * Check if an app can be opened with the given URL.
   *
   * On iOS you must declare the URL schemes you pass to this method by adding
   * the `LSApplicationQueriesSchemes` key to your app's `Info.plist` file.
   * Learn more about configuring
   * [`Info.plist`](https://capacitorjs.com/docs/ios/configuration#configuring-infoplist).
   *
   * This method always returns false for undeclared schemes, whether or not an
   * appropriate app is installed. To learn more about the key, see
   * [LSApplicationQueriesSchemes](https://developer.apple.com/library/archive/documentation/General/Reference/InfoPlistKeyReference/Articles/LaunchServicesKeys.html#//apple_ref/doc/plist/info/LSApplicationQueriesSchemes).
   *
   * On Android the URL can be a known URLScheme or an app package name.
   *
   * On [Android 11](https://developer.android.com/about/versions/11/privacy/package-visibility)
   * and newer you have to add the app package names or url schemes you want to query in the `AndroidManifest.xml`
   * inside the `queries` tag.
   *
   * @since 1.0.0
   */
  canOpenUrl(options: CanOpenURLOptions): Promise<CanOpenURLResult>;

  /**
   * Open an app with the given URL.
   * On iOS the URL should be a known URLScheme.
   * On Android the URL can be a known URLScheme or an app package name.
   *
   * @since 1.0.0
   */
  openUrl(options: OpenURLOptions): Promise<OpenURLResult>;
}

export interface CanOpenURLOptions {
  url: string;
}

export interface CanOpenURLResult {
  value: boolean;
}

export interface OpenURLOptions {
  url: string;
}

export interface OpenURLResult {
  completed: boolean;
}
