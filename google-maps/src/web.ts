import { WebPlugin } from '@capacitor/core';

import type {
  AccElementsArgs,
  AddMarkerArgs,
  CameraArgs,
  CapacitorGoogleMapsPlugin,
  CreateMapArgs,
  CurrentLocArgs,
  DestroyMapArgs,
  IndoorMapArgs,
  MapTypeArgs,
  PaddingArgs,
  RemoveMarkerArgs,
  TrafficLayerArgs,
} from './implementation';

export class CapacitorGoogleMapsWeb
  extends WebPlugin
  implements CapacitorGoogleMapsPlugin
{
  setCamera(_args: CameraArgs): Promise<void> {
    throw new Error('Method not implemented.');
  }
  setMapType(_args: MapTypeArgs): Promise<void> {
    throw new Error('Method not implemented.');
  }
  enableIndoorMaps(_args: IndoorMapArgs): Promise<void> {
    throw new Error('Method not implemented.');
  }
  enableTrafficLayer(_args: TrafficLayerArgs): Promise<void> {
    throw new Error('Method not implemented.');
  }
  enableAccessibilityElements(_args: AccElementsArgs): Promise<void> {
    throw new Error('Method not implemented.');
  }
  enableCurrentLocation(_args: CurrentLocArgs): Promise<void> {
    throw new Error('Method not implemented.');
  }
  setPadding(_args: PaddingArgs): Promise<void> {
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
