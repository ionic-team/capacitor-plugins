import { WebPlugin } from '@capacitor/core';

import type {
  AccElementsArgs,
  AddMarkerArgs,
  CameraArgs,
  AddMarkersArgs,
  CapacitorGoogleMapsPlugin,
  CreateMapArgs,
  CurrentLocArgs,
  DestroyMapArgs,
  IndoorMapArgs,
  MapTypeArgs,
  PaddingArgs,
  RemoveMarkerArgs,
  TrafficLayerArgs,
  RemoveMarkersArgs,
  OnScrollArgs,
} from './implementation';

export class CapacitorGoogleMapsWeb
  extends WebPlugin
  implements CapacitorGoogleMapsPlugin
{
  onScroll(_args: OnScrollArgs): Promise<void> {
    throw new Error('Method not implemented.');
  }
  private gMapsRef: typeof google.maps | undefined = undefined;
  private maps: {
    [id: string]: { element: HTMLElement; map: google.maps.Map };
  } = {};

  private async importGoogleLib(apiKey: string) {
    if (this.gMapsRef === undefined) {
      const lib = await import('@googlemaps/js-api-loader');
      const loader = new lib.Loader({
        apiKey: apiKey ?? '',
        version: 'weekly',
        libraries: ['places'],
      });
      const google = await loader.load();
      this.gMapsRef = google.maps;
      console.log('Loaded google maps API');
    }
  }

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
  
  // setOnCameraIdleListener(_args: { callback?: MapListenerCallback }): Promise<void> {
  //   throw new Error('Method not implemented.');
  // }

  // setOnCameraMoveStartedListener(_args: { callback?: MapListenerCallback }): Promise<void> {
  //   throw new Error('Method not implemented.');
  // }

  // setOnClusterClickListener(_args: { callback?: MapListenerCallback }): Promise<void> {
  //   throw new Error('Method not implemented.');
  // }
  
  // setOnMapClickListener(_args: { callback?: MapListenerCallback }): Promise<void> {
  //   throw new Error('Method not implemented.');
  // }

  // setOnMarkerClickListener(_args: { callback?: MapListenerCallback }): Promise<void> {
  //   throw new Error('Method not implemented.');
  // }

  // setOnMyLocationButtonClickListener(_args: { callback?: MapListenerCallback }): Promise<void> {
  //   throw new Error('Method not implemented.');
  // }

  // setOnMyLocationClickListener(_args: { callback?: MapListenerCallback }): Promise<void> {
  //   throw new Error('Method not implemented.');
  // }

  async create(args: CreateMapArgs): Promise<void> {
    console.log(`Create map: ${args.id}`);
    await this.importGoogleLib(args.apiKey);
    this.maps[args.id] = {
      map: new window.google.maps.Map(args.element, { ...args.config }),
      element: args.element,
    };
  }
  async destroy(_args: DestroyMapArgs): Promise<void> {
    console.log(`Destroy map: ${_args.id}`);
    const mapItem = this.maps[_args.id];
    mapItem.element.innerHTML = '';
    mapItem.map.unbindAll();
    delete this.maps[_args.id];
  }
}
