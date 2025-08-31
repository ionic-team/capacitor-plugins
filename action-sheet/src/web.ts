import { WebPlugin } from '@capacitor/core';

import type {
  ActionSheetPlugin,
  ShowActionsResult,
  ShowActionsOptions,
} from './definitions';

export class ActionSheetWeb extends WebPlugin implements ActionSheetPlugin {
  async showActions(options: ShowActionsOptions): Promise<ShowActionsResult> {
    return new Promise<ShowActionsResult>((resolve, _reject) => {
      let actionSheet: any = document.querySelector('pwa-action-sheet');
      if (!actionSheet) {
        actionSheet = document.createElement('pwa-action-sheet');
        document.body.appendChild(actionSheet);
      }
      actionSheet.header = options.title;
      actionSheet.cancelable = options.cancelable;
      actionSheet.options = options.options;
      actionSheet.addEventListener('onSelection', async (e: any) => {
        const selection = e.detail;
        resolve({
          index: selection,
          canceled: false,
        });
      });
      if (options.cancelable) {
        actionSheet.addEventListener('onCanceled', async () => {
          resolve({
            index: -1,
            canceled: true,
          });
        });
      }
    });
  }
}
