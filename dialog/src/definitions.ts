declare module '@capacitor/core' {
  interface PluginRegistry {
    Dialog: DialogPlugin;
  }
}

export interface AlertOptions {
  title: string;
  message: string;
  buttonTitle?: string;
}

export interface PromptOptions {
  title: string;
  message: string;
  okButtonTitle?: string;
  cancelButtonTitle?: string;
  inputPlaceholder?: string;
  inputText?: string;
}

export interface ConfirmOptions {
  title: string;
  message: string;
  okButtonTitle?: string;
  cancelButtonTitle?: string;
}

export interface PromptResult {
  value: string;
  cancelled: boolean;
}

export interface ConfirmResult {
  value: boolean;
}

export interface DialogPlugin {
  /**
   * Show an alert modal
   */
  alert(options: AlertOptions): Promise<void>;
  /**
   * Show a prompt modal
   */
  prompt(options: PromptOptions): Promise<PromptResult>;
  /**
   * Show a confirmation modal
   */
  confirm(options: ConfirmOptions): Promise<ConfirmResult>;
}
