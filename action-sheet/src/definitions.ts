declare module '@capacitor/core' {
  interface PluginRegistry {
    ActionSheet: ActionSheetPlugin;
  }
}

export interface ActionSheetOptions {
  /**
   * The title of the Action Sheet.
   *
   * @since 1.0.0
   */
  title: string;

  /**
   * A message to show under the title.
   *
   * This option is only supported on iOS.
   *
   * @since 1.0.0
   */
  message?: string;

  /**
   * Options the user can choose from.
   *
   * @since 1.0.0
   */
  options: ActionSheetOption[];
}

export enum ActionSheetOptionStyle {
  /**
   * Default style of the option.
   *
   * @since 1.0.0
   */
  Default = 'DEFAULT',

  /**
   * Style to use on destructive options.
   *
   * @since 1.0.0
   */
  Destructive = 'DESTRUCTIVE',

  /**
   * Style to use on the option that cancels the Action Sheet.
   * If used, should be on the latest availabe option.
   *
   * @since 1.0.0
   */
  Cancel = 'CANCEL',
}

export interface ActionSheetOption {
  /**
   * The title of the option
   *
   * @since 1.0.0
   */
  title: string;

  /**
   * The style of the option
   *
   * This option is only supported on iOS.
   *
   * @since 1.0.0
   */
  style?: ActionSheetOptionStyle;

  /**
   * Icon for the option (ionicon naming convention)
   *
   * This option is only supported on Web.
   *
   * @since 1.0.0
   */
  icon?: string;
}

export interface ActionSheetResult {
  /**
   * The index of the clicked option (Zero-based)
   *
   * @since 1.0.0
   */
  index: number;
}

export interface ActionSheetPlugin {
  /**
   * Show an Action Sheet style modal with various options for the user
   * to select.
   *
   * @since 1.0.0
   */
  showActions(options: ActionSheetOptions): Promise<ActionSheetResult>;
}
