import { WebPlugin } from '@capacitor/core';
import { CapacitorGoogleMapsPlugin, CreateMapArgs, DestroyMapArgs } from './implementation';

export class CapacitorGoogleMapsWeb
  extends WebPlugin
  implements CapacitorGoogleMapsPlugin
{
  create(_args: CreateMapArgs): Promise<void> {
    throw new Error('Method not implemented.');
  }
  
  destroy(_args: DestroyMapArgs): Promise<void> {
    throw new Error('Method not implemented.');
  }
}
