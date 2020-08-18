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
    return Promise.resolve();
  }

  async prefetch(_options: BrowserPrefetchOptions): Promise<void> {
    // Does nothing
    return Promise.resolve();
  }

  async close() {
    this._lastWindow && this._lastWindow.close();
    return Promise.resolve();
  }
}

const Browser = new BrowserWeb();

export { Browser };
