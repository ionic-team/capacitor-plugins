import { WebPlugin } from '@capacitor/core';
import { TextZoomPlugin } from './definitions';

export class TextZoomWeb extends WebPlugin implements TextZoomPlugin {
  constructor() {
    super({ name: 'TextZoom' });
  }

  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
