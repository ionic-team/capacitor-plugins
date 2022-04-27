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
  MapListenerCallback,
} from './implementation';

export class CapacitorGoogleMapsWeb
  extends WebPlugin
  implements CapacitorGoogleMapsPlugin
{
  private gMapsRef: typeof google.maps | undefined = undefined;
  private maps: {
    [id: string]: {
      element: HTMLElement;
      map: google.maps.Map;
      trafficLayer?: google.maps.TrafficLayer;
    };
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

  async setCamera(_args: CameraArgs): Promise<void> {
    // Animation not supported yet...
    this.maps[_args.id].map.moveCamera({
      center: _args.config.coordinate,
      heading: _args.config.bearing,
      tilt: _args.config.angle,
      zoom: _args.config.zoom,
    });
  }
  
  async setMapType(_args: MapTypeArgs): Promise<void> {
    this.maps[_args.id].map.setMapTypeId(_args.mapType);
  }
  
  async enableIndoorMaps(_args: IndoorMapArgs): Promise<void> {
    throw new Error('Method not supported on web.');
  }
  
  async enableTrafficLayer(_args: TrafficLayerArgs): Promise<void> {
    const trafficLayer = this.maps[_args.id].trafficLayer ?? new google.maps.TrafficLayer();
    
    if (_args.enabled) {
      trafficLayer.setMap(this.maps[_args.id].map);
      this.maps[_args.id].trafficLayer = trafficLayer;
    }
    else if(this.maps[_args.id].trafficLayer) {
      trafficLayer.setMap(null);
      this.maps[_args.id].trafficLayer = undefined;
    }
  }
  
  async enableAccessibilityElements(_args: AccElementsArgs): Promise<void> {
    throw new Error('Method not supported on web.');
  }
  
  async enableCurrentLocation(_args: CurrentLocArgs): Promise<void> {
    if (_args.enabled) {
      if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
          (position: GeolocationPosition) => {
            const pos = {
              lat: position.coords.latitude,
              lng: position.coords.longitude,
            };

            this.maps[_args.id].map.setCenter(pos);
          },
          () => {
            throw new Error('Geolocation not supported on web browser.');
          }
        );
      } else {
        throw new Error('Geolocation not supported on web browser.');
      }
    }
  }
  async setPadding(_args: PaddingArgs): Promise<void> {
    const bounds = this.maps[_args.id].map.getBounds();
    
    if (bounds !== undefined) {
      this.maps[_args.id].map.fitBounds(bounds, _args.padding); 
    }
  }
  async addMarkers(_args: AddMarkersArgs): Promise<{ ids: string[] }> {
    throw new Error('Method not implemented.');
  }
  
  async removeMarkers(_args: RemoveMarkersArgs): Promise<void> {
    throw new Error('Method not implemented.');
  }
  async enableClustering(_args: { id: string }): Promise<void> {
    throw new Error('Method not implemented.');
  }
  
  async disableClustering(_args: { id: string }): Promise<void> {
    throw new Error('Method not implemented.');
  }
  
  async addMarker(_args: AddMarkerArgs): Promise<{ id: string }> {
    throw new Error('Method not implemented.');
  }
  
  async removeMarker(_args: RemoveMarkerArgs): Promise<void> {
    throw new Error('Method not implemented.');
  }
  
  async onScroll(_args: OnScrollArgs): Promise<void> {
    throw new Error('Method not supported on web.');
  }

  async create(_args: CreateMapArgs): Promise<void> {
    console.log(`Create map: ${_args.id}`);
    await this.importGoogleLib(_args.apiKey);
    this.maps[_args.id] = {
      map: new window.google.maps.Map(_args.element, { ..._args.config }),
      element: _args.element,
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
