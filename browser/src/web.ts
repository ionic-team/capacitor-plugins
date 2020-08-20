import { WebPlugin } from '@capacitor/core';
import {
  BrowserPlugin,
  BrowserOpenOptions,
  BrowserPrefetchOptions,
} from './definitions';

export class BrowserWeb extends WebPlugin implements BrowserPlugin {
  _lastWindow: Window | null;

  constructor() {
    super({ name: 'Browser' });
    this._lastWindow = null;
  }

  async open(options: BrowserOpenOptions): Promise<void> {
    this._lastWindow = window.open(options.url, options.windowName || '_blank');
  }

  async prefetch(_options: BrowserPrefetchOptions): Promise<void> {
    // Does nothing
  }

  async close(): Promise<void> {
    if (this._lastWindow != null) {
      this._lastWindow.close();
      this._lastWindow = null;
    }
  }
}

const Browser = new BrowserWeb();

export { Browser };
