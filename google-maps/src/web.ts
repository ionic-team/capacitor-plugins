import { WebPlugin } from '@capacitor/core';

import type {
  AddMarkerArgs,
  CapacitorGoogleMapsPlugin,
  CreateMapArgs,
  DestroyMapArgs,
  RemoveMarkerArgs,
} from './implementation';

export class CapacitorGoogleMapsWeb
  extends WebPlugin
  implements CapacitorGoogleMapsPlugin
{
  addMarker(_args: AddMarkerArgs): Promise<{ id: string; }> {
    throw new Error('Method not implemented.');
  }
  removeMarker(_args: RemoveMarkerArgs): Promise<void> {
    throw new Error('Method not implemented.');
  }
  create(_args: CreateMapArgs): Promise<void> {
    throw new Error('Method not implemented.');
  }
  destroy(_args: DestroyMapArgs): Promise<void> {
    throw new Error('Method not implemented.');
  }
}
