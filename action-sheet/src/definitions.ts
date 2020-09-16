declare module '@capacitor/core' {
  interface PluginRegistry {
    ActionSheet: ActionSheetPlugin;
  }
}

export interface ActionSheetOptions {
  title: string;
  /**
   * iOS only
   */
  message?: string;
  options: ActionSheetOption[];
}

export enum ActionSheetOptionStyle {
  Default = 'DEFAULT',
  Destructive = 'DESTRUCTIVE',
  Cancel = 'CANCEL',
}

export interface ActionSheetOption {
  title: string;
  style?: ActionSheetOptionStyle;
  /**
   * Icon for web (ionicon naming convention)
   */
  icon?: string;
}

export interface ActionSheetResult {
  index: number;
}

export interface ActionSheetPlugin {
  /**
   * Show an Action Sheet style modal with various options for the user
   * to select.
   */
  showActions(options: ActionSheetOptions): Promise<ActionSheetResult>;
}
