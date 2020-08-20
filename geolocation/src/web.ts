import { WebPlugin } from '@capacitor/core';
import { GeolocationPluginPlugin } from './definitions';

export class GeolocationPluginWeb extends WebPlugin
  implements GeolocationPluginPlugin {
  constructor() {
    super({
      name: 'GeolocationPlugin',
      platforms: ['web'],
    });
  }

  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}

const GeolocationPlugin = new GeolocationPluginWeb();

export { GeolocationPlugin };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(GeolocationPlugin);
