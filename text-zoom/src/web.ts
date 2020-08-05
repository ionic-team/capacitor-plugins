import { WebPlugin } from '@capacitor/core';
import { TextZoomPlugin } from './definitions';

export class TextZoomWeb extends WebPlugin implements TextZoomPlugin {
  constructor() {
    super({ name: 'TextZoom' });
  }

  async get(): Promise<{ value: number }> {
    throw new Error('TODO');
  }

  async getPreferred(): Promise<{ value: number }> {
    throw new Error('TODO');
  }

  async set(options: { value: number }): Promise<void> {
    options;
    throw new Error('TODO');
  }
}
