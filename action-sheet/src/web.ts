import { WebPlugin } from '@capacitor/core';

import type {
  ActionSheetOptions,
  ActionSheetPlugin,
  ActionSheetResult,
} from './definitions';

export class ActionSheetWeb extends WebPlugin implements ActionSheetPlugin {
  constructor() {
    super({ name: 'ActionSheet' });
  }

  async showActions(options: ActionSheetOptions): Promise<ActionSheetResult> {
    return new Promise<ActionSheetResult>((resolve, _reject) => {
      let actionSheet: any = document.querySelector('pwa-action-sheet');
      if (!actionSheet) {
        actionSheet = document.createElement('pwa-action-sheet');
        document.body.appendChild(actionSheet);
      }
      actionSheet.header = options.title;
      actionSheet.cancelable = false;
      actionSheet.options = options.options;
      actionSheet.addEventListener('onSelection', async (e: any) => {
        const selection = e.detail;
        resolve({
          index: selection,
        });
      });
    });
  }
}
