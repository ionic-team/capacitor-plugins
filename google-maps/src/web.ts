import { WebPlugin } from '@capacitor/core';

import type { CapacitorGoogleMapsPlugin } from './definitions';

export class CapacitorGoogleMapsWeb extends WebPlugin implements CapacitorGoogleMapsPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
