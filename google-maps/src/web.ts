import { WebPlugin } from '@capacitor/core';

import type {
  CapacitorGoogleMapsPlugin,
  CreateMapArgs,
  DestroyMapArgs,
  InitializeMapArgs,
} from './implementation';

export class CapacitorGoogleMapsWeb
  extends WebPlugin
  implements CapacitorGoogleMapsPlugin
{
  initialize(_args: InitializeMapArgs): Promise<void> {
    throw new Error('Method not implemented.');
  }

  create(_args: CreateMapArgs): Promise<void> {
    throw new Error('Method not implemented.');
  }

  destroy(_args: DestroyMapArgs): Promise<void> {
    throw new Error('Method not implemented.');
  }
}
