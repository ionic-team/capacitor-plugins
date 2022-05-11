import { WebPlugin } from '@capacitor/core';

import type { FetchOptions } from './definitions';
import type { CapacitorNHttpPlugin } from './implementation';

export class CapacitorNHttpPluginWeb extends WebPlugin implements CapacitorNHttpPlugin {
  async fetch(options: FetchOptions): Promise<Response> {
    try {
      const response = await window['webFetch'](options.resource, options.config);
    
      if (!response.ok) {
        return Promise.reject(response);
      }

      return response;
    }
    catch (error) {
      return Promise.reject(error);
    }
  }
}
