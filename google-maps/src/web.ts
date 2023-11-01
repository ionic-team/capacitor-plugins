import { WebPlugin } from '@capacitor/core';
import type {
  Cluster,
  onClusterClickHandler,
} from '@googlemaps/markerclusterer';
import {
  MarkerClusterer,
  SuperClusterAlgorithm,
} from '@googlemaps/markerclusterer';

import type { Marker } from './definitions';
import { MapType, LatLngBounds } from './definitions';
import type {
  AddMarkerArgs,
  CameraArgs,
  AddMarkersArgs,
  CapacitorGoogleMapsPlugin,
  CreateMapArgs,
  CurrentLocArgs,
  DestroyMapArgs,
  MapTypeArgs,
  PaddingArgs,
  RemoveMarkerArgs,
  TrafficLayerArgs,
  RemoveMarkersArgs,
  MapBoundsContainsArgs,
  EnableClusteringArgs,
  FitBoundsArgs,
  MapBoundsExtendArgs,
  AddPolygonsArgs,
  RemovePolygonsArgs,
  AddCirclesArgs,
  RemoveCirclesArgs,
  AddPolylinesArgs,
  RemovePolylinesArgs,
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
      polygons: {
        [id: string]: google.maps.Polygon;
      };
      circles: {
        [id: string]: google.maps.Circle;
      };
      polylines: {
        [id: string]: google.maps.Polyline;
      };
      markerClusterer?: MarkerClusterer;
      trafficLayer?: google.maps.TrafficLayer;
    };
  } = {};
  private currMarkerId = 0;
  private currPolygonId = 0;
  private currCircleId = 0;
  private currPolylineId = 0;

  private onClusterClickHandler: onClusterClickHandler = (
    _: google.maps.MapMouseEvent,
    cluster: Cluster,
    map: google.maps.Map,
  ): void => {
    const mapId = this.getIdFromMap(map);
    const items: any[] = [];

    if (cluster.markers != undefined) {
      for (const marker of cluster.markers) {
        const markerId = this.getIdFromMarker(mapId, marker);

        items.push({
          markerId: markerId,
          latitude: marker.getPosition()?.lat(),
          longitude: marker.getPosition()?.lng(),
          title: marker.getTitle(),
          snippet: '',
        });
      }
    }

    this.notifyListeners('onClusterClick', {
      mapId: mapId,
      latitude: cluster.position.lat(),
      longitude: cluster.position.lng(),
      size: cluster.count,
      items: items,
    });
  };

  private getIdFromMap(map: google.maps.Map): string {
    for (const id in this.maps) {
      if (this.maps[id].map == map) {
        return id;
      }
    }

    return '';
  }

  private getIdFromMarker(mapId: string, marker: google.maps.Marker): string {
    for (const id in this.maps[mapId].markers) {
      if (this.maps[mapId].markers[id] == marker) {
        return id;
      }
    }

    return '';
  }

  private async importGoogleLib(
    apiKey: string,
    region?: string,
    language?: string,
  ) {
    if (this.gMapsRef === undefined) {
      const lib = await import('@googlemaps/js-api-loader');
      const loader = new lib.Loader({
        apiKey: apiKey ?? '',
        version: 'weekly',
        libraries: ['places'],
        language,
        region,
      });
      const google = await loader.load();
      this.gMapsRef = google.maps;
      console.log('Loaded google maps API');
    }
  }

  async enableTouch(_args: { id: string }): Promise<void> {
    this.maps[_args.id].map.setOptions({ gestureHandling: 'auto' });
  }

  async disableTouch(_args: { id: string }): Promise<void> {
    this.maps[_args.id].map.setOptions({ gestureHandling: 'none' });
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

  async getMapType(_args: { id: string }): Promise<{ type: string }> {
    let type = this.maps[_args.id].map.getMapTypeId();
    if (type !== undefined) {
      if (type === 'roadmap') {
        type = MapType.Normal;
      }
      return { type: `${type.charAt(0).toUpperCase()}${type.slice(1)}` };
    }
    throw new Error('Map type is undefined');
  }

  async setMapType(_args: MapTypeArgs): Promise<void> {
    let mapType = _args.mapType.toLowerCase();
    if (_args.mapType === MapType.Normal) {
      mapType = 'roadmap';
    }
    this.maps[_args.id].map.setMapTypeId(mapType);
  }

  async enableIndoorMaps(): Promise<void> {
    throw new Error('Method not supported on web.');
  }

  async enableTrafficLayer(_args: TrafficLayerArgs): Promise<void> {
    const trafficLayer =
      this.maps[_args.id].trafficLayer ?? new google.maps.TrafficLayer();

    if (_args.enabled) {
      trafficLayer.setMap(this.maps[_args.id].map);
      this.maps[_args.id].trafficLayer = trafficLayer;
    } else if (this.maps[_args.id].trafficLayer) {
      trafficLayer.setMap(null);
      this.maps[_args.id].trafficLayer = undefined;
    }
  }

  async enableAccessibilityElements(): Promise<void> {
    throw new Error('Method not supported on web.');
  }

  dispatchMapEvent(): Promise<void> {
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

            this.notifyListeners('onMyLocationButtonClick', {});

            this.notifyListeners('onMyLocationClick', {});
          },
          () => {
            throw new Error('Geolocation not supported on web browser.');
          },
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

  async getMapBounds(_args: { id: string }): Promise<LatLngBounds> {
    const bounds = this.maps[_args.id].map.getBounds();

    if (!bounds) {
      throw new Error('Google Map Bounds could not be found.');
    }

    return new LatLngBounds({
      southwest: {
        lat: bounds.getSouthWest().lat(),
        lng: bounds.getSouthWest().lng(),
      },
      center: {
        lat: bounds.getCenter().lat(),
        lng: bounds.getCenter().lng(),
      },
      northeast: {
        lat: bounds.getNorthEast().lat(),
        lng: bounds.getNorthEast().lng(),
      },
    });
  }

  async fitBounds(_args: FitBoundsArgs): Promise<void> {
    const map = this.maps[_args.id].map;
    const bounds = this.getLatLngBounds(_args.bounds);
    map.fitBounds(bounds, _args.padding);
  }

  async addMarkers(_args: AddMarkersArgs): Promise<{ ids: string[] }> {
    const markerIds: string[] = [];
    const map = this.maps[_args.id];

    for (const markerArgs of _args.markers) {
      const markerOpts = this.buildMarkerOpts(markerArgs, map.map);
      const marker = new google.maps.Marker(markerOpts);

      const id = '' + this.currMarkerId;

      map.markers[id] = marker;
      this.setMarkerListeners(_args.id, id, marker);

      markerIds.push(id);
      this.currMarkerId++;
    }

    return { ids: markerIds };
  }

  async addMarker(_args: AddMarkerArgs): Promise<{ id: string }> {
    const markerOpts = this.buildMarkerOpts(
      _args.marker,
      this.maps[_args.id].map,
    );

    const marker = new google.maps.Marker(markerOpts);

    const id = '' + this.currMarkerId;

    this.maps[_args.id].markers[id] = marker;
    this.setMarkerListeners(_args.id, id, marker);

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

  async addPolygons(args: AddPolygonsArgs): Promise<{ ids: string[] }> {
    const polygonIds: string[] = [];
    const map = this.maps[args.id];

    for (const polygonArgs of args.polygons) {
      const polygon = new google.maps.Polygon(polygonArgs);
      polygon.setMap(map.map);

      const id = '' + this.currPolygonId;
      this.maps[args.id].polygons[id] = polygon;
      this.setPolygonListeners(args.id, id, polygon);

      polygonIds.push(id);
      this.currPolygonId++;
    }

    return { ids: polygonIds };
  }

  async removePolygons(args: RemovePolygonsArgs): Promise<void> {
    const map = this.maps[args.id];

    for (const id of args.polygonIds) {
      map.polygons[id].setMap(null);
      delete map.polygons[id];
    }
  }

  async addCircles(args: AddCirclesArgs): Promise<{ ids: string[] }> {
    const circleIds: string[] = [];
    const map = this.maps[args.id];

    for (const circleArgs of args.circles) {
      const circle = new google.maps.Circle(circleArgs);
      circle.setMap(map.map);

      const id = '' + this.currCircleId;
      this.maps[args.id].circles[id] = circle;
      this.setCircleListeners(args.id, id, circle);

      circleIds.push(id);
      this.currCircleId++;
    }

    return { ids: circleIds };
  }

  async removeCircles(args: RemoveCirclesArgs): Promise<void> {
    const map = this.maps[args.id];

    for (const id of args.circleIds) {
      map.circles[id].setMap(null);
      delete map.circles[id];
    }
  }

  async addPolylines(args: AddPolylinesArgs): Promise<{ ids: string[] }> {
    const lineIds: string[] = [];
    const map = this.maps[args.id];

    for (const polylineArgs of args.polylines) {
      const polyline = new google.maps.Polyline(polylineArgs);
      polyline.set('tag', polylineArgs.tag);
      polyline.setMap(map.map);

      const id = '' + this.currPolylineId;
      this.maps[args.id].polylines[id] = polyline;
      this.setPolylineListeners(args.id, id, polyline);

      lineIds.push(id);
      this.currPolylineId++;
    }

    return {
      ids: lineIds,
    };
  }

  async removePolylines(args: RemovePolylinesArgs): Promise<void> {
    const map = this.maps[args.id];

    for (const id of args.polylineIds) {
      map.polylines[id].setMap(null);
      delete map.polylines[id];
    }
  }

  async enableClustering(_args: EnableClusteringArgs): Promise<void> {
    const markers: google.maps.Marker[] = [];

    for (const id in this.maps[_args.id].markers) {
      markers.push(this.maps[_args.id].markers[id]);
    }

    this.maps[_args.id].markerClusterer = new MarkerClusterer({
      map: this.maps[_args.id].map,
      markers: markers,
      algorithm: new SuperClusterAlgorithm({
        minPoints: _args.minClusterSize ?? 4,
      }),
      onClusterClick: this.onClusterClickHandler,
    });
  }

  async disableClustering(_args: { id: string }): Promise<void> {
    this.maps[_args.id].markerClusterer?.setMap(null);
    this.maps[_args.id].markerClusterer = undefined;
  }

  async onScroll(): Promise<void> {
    throw new Error('Method not supported on web.');
  }

  async onResize(): Promise<void> {
    throw new Error('Method not supported on web.');
  }

  async onDisplay(): Promise<void> {
    throw new Error('Method not supported on web.');
  }

  async create(_args: CreateMapArgs): Promise<void> {
    console.log(`Create map: ${_args.id}`);
    await this.importGoogleLib(_args.apiKey, _args.region, _args.language);

    this.maps[_args.id] = {
      map: new window.google.maps.Map(_args.element, { ..._args.config }),
      element: _args.element,
      markers: {},
      polygons: {},
      circles: {},
      polylines: {},
    };
    this.setMapListeners(_args.id);
  }

  async destroy(_args: DestroyMapArgs): Promise<void> {
    console.log(`Destroy map: ${_args.id}`);
    const mapItem = this.maps[_args.id];
    mapItem.element.innerHTML = '';
    mapItem.map.unbindAll();
    delete this.maps[_args.id];
  }

  async mapBoundsContains(
    _args: MapBoundsContainsArgs,
  ): Promise<{ contains: boolean }> {
    const bounds = this.getLatLngBounds(_args.bounds);
    const point = new google.maps.LatLng(_args.point.lat, _args.point.lng);
    return { contains: bounds.contains(point) };
  }

  async mapBoundsExtend(
    _args: MapBoundsExtendArgs,
  ): Promise<{ bounds: LatLngBounds }> {
    const bounds = this.getLatLngBounds(_args.bounds);
    const point = new google.maps.LatLng(_args.point.lat, _args.point.lng);
    bounds.extend(point);
    const result = new LatLngBounds({
      southwest: {
        lat: bounds.getSouthWest().lat(),
        lng: bounds.getSouthWest().lng(),
      },
      center: {
        lat: bounds.getCenter().lat(),
        lng: bounds.getCenter().lng(),
      },
      northeast: {
        lat: bounds.getNorthEast().lat(),
        lng: bounds.getNorthEast().lng(),
      },
    });
    return { bounds: result };
  }

  private getLatLngBounds(_args: LatLngBounds): google.maps.LatLngBounds {
    return new google.maps.LatLngBounds(
      new google.maps.LatLng(_args.southwest.lat, _args.southwest.lng),
      new google.maps.LatLng(_args.northeast.lat, _args.northeast.lng),
    );
  }

  async setCircleListeners(
    mapId: string,
    circleId: string,
    circle: google.maps.Circle,
  ): Promise<void> {
    circle.addListener('click', () => {
      this.notifyListeners('onCircleClick', {
        mapId: mapId,
        circleId: circleId,
        tag: circle.get('tag'),
      });
    });
  }

  async setPolygonListeners(
    mapId: string,
    polygonId: string,
    polygon: google.maps.Polygon,
  ): Promise<void> {
    polygon.addListener('click', () => {
      this.notifyListeners('onPolygonClick', {
        mapId: mapId,
        polygonId: polygonId,
        tag: polygon.get('tag'),
      });
    });
  }

  async setPolylineListeners(
    mapId: string,
    polylineId: string,
    polyline: google.maps.Polyline,
  ): Promise<void> {
    polyline.addListener('click', () => {
      this.notifyListeners('onPolylineClick', {
        mapId: mapId,
        polylineId: polylineId,
        tag: polyline.get('tag'),
      });
    });
  }

  async setMarkerListeners(
    mapId: string,
    markerId: string,
    marker: google.maps.Marker,
  ): Promise<void> {
    marker.addListener('click', () => {
      this.notifyListeners('onMarkerClick', {
        mapId: mapId,
        markerId: markerId,
        latitude: marker.getPosition()?.lat(),
        longitude: marker.getPosition()?.lng(),
        title: marker.getTitle(),
        snippet: '',
      });
    });

    marker.addListener('dragstart', () => {
      this.notifyListeners('onMarkerDragStart', {
        mapId: mapId,
        markerId: markerId,
        latitude: marker.getPosition()?.lat(),
        longitude: marker.getPosition()?.lng(),
        title: marker.getTitle(),
        snippet: '',
      });
    });

    marker.addListener('drag', () => {
      this.notifyListeners('onMarkerDrag', {
        mapId: mapId,
        markerId: markerId,
        latitude: marker.getPosition()?.lat(),
        longitude: marker.getPosition()?.lng(),
        title: marker.getTitle(),
        snippet: '',
      });
    });

    marker.addListener('dragend', () => {
      this.notifyListeners('onMarkerDragEnd', {
        mapId: mapId,
        markerId: markerId,
        latitude: marker.getPosition()?.lat(),
        longitude: marker.getPosition()?.lng(),
        title: marker.getTitle(),
        snippet: '',
      });
    });
  }

  async setMapListeners(mapId: string): Promise<void> {
    const map = this.maps[mapId].map;

    map.addListener('idle', async () => {
      const bounds = await this.getMapBounds({ id: mapId });
      this.notifyListeners('onCameraIdle', {
        mapId: mapId,
        bearing: map.getHeading(),
        bounds: bounds,
        latitude: map.getCenter()?.lat(),
        longitude: map.getCenter()?.lng(),
        tilt: map.getTilt(),
        zoom: map.getZoom(),
      });
    });

    map.addListener('center_changed', () => {
      this.notifyListeners('onCameraMoveStarted', {
        mapId: mapId,
        isGesture: true,
      });
    });

    map.addListener('bounds_changed', async () => {
      const bounds = await this.getMapBounds({ id: mapId });
      this.notifyListeners('onBoundsChanged', {
        mapId: mapId,
        bearing: map.getHeading(),
        bounds: bounds,
        latitude: map.getCenter()?.lat(),
        longitude: map.getCenter()?.lng(),
        tilt: map.getTilt(),
        zoom: map.getZoom(),
      });
    });

    map.addListener(
      'click',
      (e: google.maps.MapMouseEvent | google.maps.IconMouseEvent) => {
        this.notifyListeners('onMapClick', {
          mapId: mapId,
          latitude: e.latLng?.lat(),
          longitude: e.latLng?.lng(),
        });
      },
    );

    this.notifyListeners('onMapReady', {
      mapId: mapId,
    });
  }

  private buildMarkerOpts(
    marker: Marker,
    map: google.maps.Map,
  ): google.maps.MarkerOptions {
    let iconImage: google.maps.Icon | undefined = undefined;
    if (marker.iconUrl) {
      iconImage = {
        url: marker.iconUrl,
        scaledSize: marker.iconSize
          ? new google.maps.Size(marker.iconSize.width, marker.iconSize.height)
          : null,
        anchor: marker.iconAnchor
          ? new google.maps.Point(marker.iconAnchor.x, marker.iconAnchor.y)
          : new google.maps.Point(0, 0),
        origin: marker.iconOrigin
          ? new google.maps.Point(marker.iconOrigin.x, marker.iconOrigin.y)
          : new google.maps.Point(0, 0),
      };
    }

    const opts: google.maps.MarkerOptions = {
      position: marker.coordinate,
      map: map,
      opacity: marker.opacity,
      title: marker.title,
      icon: iconImage,
      draggable: marker.draggable,
      zIndex: marker.zIndex ?? 0,
    };

    return opts;
  }
}
