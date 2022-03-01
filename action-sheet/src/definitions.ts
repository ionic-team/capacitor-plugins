export interface ShowActionsOptions {
  /**
   * The title of the Action Sheet.
   *
   * @since 1.0.0
   */
  title?: string;

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
  options: ActionSheetButton[];
}

export enum ActionSheetButtonStyle {
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

export interface ActionSheetButton {
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
  style?: ActionSheetButtonStyle;

  /**
   * Icon for the option (ionicon naming convention)
   *
   * This option is only supported on Web.
   *
   * @since 1.0.0
   */
  icon?: string;
}

export interface ShowActionsResult {
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
  showActions(options: ShowActionsOptions): Promise<ShowActionsResult>;
}

/**
 * @deprecated Use `ShowActionsOptions`.
 * @since 1.0.0
 */
export type ActionSheetOptions = ShowActionsOptions;

/**
 * @deprecated Use `ShowActionsResult`.
 * @since 1.0.0
 */
export type ActionSheetResult = ShowActionsResult;

/**
 * @deprecated Use `ActionSheetButton`.
 * @since 1.0.0
 */
export type ActionSheetOption = ActionSheetButton;

/**
 * @deprecated Use `ActionSheetButtonStyle`.
 * @since 1.0.0
 */
export const ActionSheetOptionStyle = ActionSheetButtonStyle;
