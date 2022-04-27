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
  private gMapsRef: typeof google.maps | undefined = undefined;
  private maps: {
    [id: string]: {
      element: HTMLElement;
      map: google.maps.Map;
      markers: {
        [id: string]: google.maps.Marker;
      };
      //markerClusterer?: google.maps.MarkerClusterer;
      trafficLayer?: google.maps.TrafficLayer;
    };
  } = {};
  private currMarkerId = 0;

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
    const markerIds: string[] = [];
    const map = this.maps[_args.id];

    for (const markerArgs of _args.markers) {
      const marker = new google.maps.Marker({
        position: markerArgs.coordinate,
        map: map.map,
        opacity: markerArgs.opacity,
        title: markerArgs.title,
        icon: markerArgs.iconUrl,
        draggable: markerArgs.draggable
      });
  
      const id = '' + this.currMarkerId;
      map.markers[id] = marker;
      markerIds.push(id);
      this.currMarkerId++;
    }

    return { ids: markerIds };
  }

  async addMarker(_args: AddMarkerArgs): Promise<{ id: string }> {
    const marker = new google.maps.Marker({
      position: _args.marker.coordinate,
      map: this.maps[_args.id].map,
      opacity: _args.marker.opacity,
      title: _args.marker.title,
      icon: _args.marker.iconUrl,
      draggable: _args.marker.draggable
    });

    const id = '' + this.currMarkerId;
    this.maps[_args.id].markers[id] = marker;
    this.currMarkerId++;

    return { id: id };
  }
  
  async removeMarkers(_args: RemoveMarkersArgs): Promise<void> {
    const map = this.maps[_args.id];

    for (const id of _args.markerIds) {
      map.markers[id].setMap(null);
      delete map.markers[id];
    }
  }

  async removeMarker(_args: RemoveMarkerArgs): Promise<void> {
    this.maps[_args.id].markers[_args.markerId].setMap(null);
    delete this.maps[_args.id].markers[_args.markerId];
  }
  
  async enableClustering(_args: { id: string }): Promise<void> {
    throw new Error('Method not implemented.');
  }
  
  async disableClustering(_args: { id: string }): Promise<void> {
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
      markers: {},
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
