import { WebPlugin } from '@capacitor/core';

import type {
  AddMarkerArgs,
  AddMarkersArgs,
  CapacitorGoogleMapsPlugin,
  CreateMapArgs,
  DestroyMapArgs,
  RemoveMarkerArgs,
  RemoveMarkersArgs,
} from './implementation';

export class CapacitorGoogleMapsWeb
  extends WebPlugin
  implements CapacitorGoogleMapsPlugin
{
  addMarkers(_args: AddMarkersArgs): Promise<{ ids: string[] }> {
    throw new Error('Method not implemented.');
  }
  removeMarkers(_args: RemoveMarkersArgs): Promise<void> {
    throw new Error('Method not implemented.');
  }
  enableClustering(_args: { id: string }): Promise<void> {
    throw new Error('Method not implemented.');
  }
  disableClustering(_args: { id: string }): Promise<void> {
    throw new Error('Method not implemented.');
  }
  addMarker(_args: AddMarkerArgs): Promise<{ id: string }> {
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
