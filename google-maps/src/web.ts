import { WebPlugin } from '@capacitor/core';
import { CapacitorGoogleMapsPlugin } from './implementation';
import { MapOptions } from './map';

export class CapacitorGoogleMapsWeb
  extends WebPlugin
  implements CapacitorGoogleMapsPlugin
{
  create(id: string, options: MapOptions): Promise<void> {
    throw new Error('Method not implemented.');
  }
  destroy(id: string): Promise<void> {
    throw new Error('Method not implemented.');
  }
}
