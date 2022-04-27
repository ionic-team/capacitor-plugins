import { Capacitor } from '@capacitor/core';
import type { PluginListenerHandle } from '@capacitor/core';

import type { CameraConfig, LatLng, MapPadding, MapType } from './definitions';
import type { CreateMapOptions, MapListenerCallback } from './implementation';
import { CapacitorGoogleMaps } from './implementation';

export interface Marker {
  coordinate: LatLng;
  opacity?: number;
  title?: string;
  snippet?: string;
  isFlat?: boolean;
  iconUrl?: string;
  draggable?: boolean;
}

class MapCustomElement extends HTMLElement {
  constructor() {
    super();
  }

  connectedCallback() {
    this.style.overflow = 'scroll';
    (this.style as any)['-webkit-overflow-scrolling'] = 'touch';

    const overflowDiv = document.createElement('div');
    overflowDiv.style.height = '200%';

    this.appendChild(overflowDiv);
  }
}

customElements.define('capacitor-google-map', MapCustomElement);

export class GoogleMap {
  private id: string;
  private element: HTMLElement | null = null;

  private onCameraIdleListener?: PluginListenerHandle;
  private onCameraMoveStartedListener?: PluginListenerHandle;
  private onClusterClickListener?: PluginListenerHandle;
  private onClusterInfoWindowClickListener?: PluginListenerHandle;
  private onInfoWindowClickListener?: PluginListenerHandle;
  private onMapClickListener?: PluginListenerHandle;
  private onMarkerClickListener?: PluginListenerHandle;
  private onMyLocationButtonClickListener?: PluginListenerHandle;
  private onMyLocationClickListener?: PluginListenerHandle;

  private constructor(id: string) {
    this.id = id;
  }

  public static async create(
    options: CreateMapOptions,
    callback?: MapListenerCallback,
  ): Promise<GoogleMap> {
    const newMap = new GoogleMap(options.id);

    if (!options.element) {
      throw new Error('container element is required');
    }

    if (options.config.androidLiteMode === undefined) {
      options.config.androidLiteMode = false;
    }

    newMap.element = options.element;

    const elementBounds = options.element.getBoundingClientRect();
    options.config.width = elementBounds.width;
    options.config.height = elementBounds.height;
    options.config.x = elementBounds.x;
    options.config.y = elementBounds.y;

    if (Capacitor.getPlatform() == 'android') {
      (options.element as any) = {};
      newMap.initScrolling();
    }

    if (Capacitor.getPlatform() == 'ios') {
      (options.element as any) = {};
    }

    await CapacitorGoogleMaps.create(options);

    if (callback) {
      CapacitorGoogleMaps.addListener('onMapReady', callback);
    }

    return newMap;
  }

  async enableClustering(): Promise<void> {
    return CapacitorGoogleMaps.enableClustering({
      id: this.id,
    });
  }

  async disableClustering(): Promise<void> {
    return CapacitorGoogleMaps.disableClustering({
      id: this.id,
    });
  }

  async addMarker(marker: Marker): Promise<string> {
    const res = await CapacitorGoogleMaps.addMarker({
      id: this.id,
      marker,
    });

    return res.id;
  }

  async addMarkers(markers: Marker[]): Promise<string[]> {
    const res = await CapacitorGoogleMaps.addMarkers({
      id: this.id,
      markers,
    });

    return res.ids;
  }

  async removeMarker(id: string): Promise<void> {
    return CapacitorGoogleMaps.removeMarker({
      id: this.id,
      markerId: id,
    });
  }

  async removeMarkers(ids: string[]): Promise<void> {
    return CapacitorGoogleMaps.removeMarkers({
      id: this.id,
      markerIds: ids,
    });
  }

  async destroy(): Promise<void> {
    if (Capacitor.getPlatform() == 'android') {
      this.disableScrolling();
    }

    this.removeAllMapListeners();

    return CapacitorGoogleMaps.destroy({
      id: this.id,
    });
  }

  async setCamera(config: CameraConfig): Promise<void> {
    return CapacitorGoogleMaps.setCamera({
      id: this.id,
      config,
    });
  }

  async setMapType(mapType: MapType): Promise<void> {
    return CapacitorGoogleMaps.setMapType({
      id: this.id,
      mapType,
    });
  }

  async enableIndoorMaps(enabled: boolean): Promise<void> {
    return CapacitorGoogleMaps.enableIndoorMaps({
      id: this.id,
      enabled,
    });
  }

  async enableTrafficLayer(enabled: boolean): Promise<void> {
    return CapacitorGoogleMaps.enableTrafficLayer({
      id: this.id,
      enabled,
    });
  }

  async enableAccessibilityElements(enabled: boolean): Promise<void> {
    return CapacitorGoogleMaps.enableAccessibilityElements({
      id: this.id,
      enabled,
    });
  }

  async enableCurrentLocation(enabled: boolean): Promise<void> {
    return CapacitorGoogleMaps.enableCurrentLocation({
      id: this.id,
      enabled,
    });
  }

  async setPadding(padding: MapPadding): Promise<void> {
    return CapacitorGoogleMaps.setPadding({
      id: this.id,
      padding,
    });
  }

  initScrolling(): void {
    const ionContents = document.getElementsByTagName('ion-content');

    // eslint-disable-next-line @typescript-eslint/prefer-for-of
    for (let i = 0; i < ionContents.length; i++) {
      (ionContents[i] as any).scrollEvents = true;
    }

    window.addEventListener('ionScroll', this.handleScrollEvent);
    window.addEventListener('scroll', this.handleScrollEvent);
    window.addEventListener('resize', this.handleScrollEvent);
    if (screen.orientation) {
      screen.orientation.addEventListener('change', () => {
        setTimeout(this.updateMapBounds, 500);
      });
    } else {
      window.addEventListener('orientationchange', () => {
        setTimeout(this.updateMapBounds, 500);
      });
    }
  }

  disableScrolling(): void {
    window.removeEventListener('ionScroll', this.handleScrollEvent);
    window.removeEventListener('scroll', this.handleScrollEvent);
    window.removeEventListener('resize', this.handleScrollEvent);
    if (screen.orientation) {
      screen.orientation.removeEventListener('change', () => {
        setTimeout(this.updateMapBounds, 1000);
      });
    } else {
      window.removeEventListener('orientationchange', () => {
        setTimeout(this.updateMapBounds, 1000);
      });
    }
  }

  handleScrollEvent = (): void => this.updateMapBounds();

  private updateMapBounds(): void {
    if (this.element) {
      const mapRect = this.element.getBoundingClientRect();

      CapacitorGoogleMaps.onScroll({
        id: this.id,
        mapBounds: {
          x: mapRect.x,
          y: mapRect.y,
          width: mapRect.width,
          height: mapRect.height,
        },
      });
    }
  }

  /*
  private findContainerElement(): HTMLElement | null {
    if (!this.element) {
      return null;
    }

    let parentElement = this.element.parentElement;
    while (parentElement !== null) {
      if (window.getComputedStyle(parentElement).overflowY !== 'hidden') {
        return parentElement;
      }

      parentElement = parentElement.parentElement;
    }

    return null;
  }
  */

  async setOnCameraIdleListener(callback?: MapListenerCallback): Promise<void> {
    if (this.onCameraIdleListener) {
      this.onCameraIdleListener.remove();
    }

    if (callback) {
      this.onCameraIdleListener = CapacitorGoogleMaps.addListener(
        'onCameraIdle',
        this.generateCallback(callback),
      );
    } else {
      this.onCameraIdleListener = undefined;
    }
  }

  async setOnCameraMoveStartedListener(
    callback?: MapListenerCallback,
  ): Promise<void> {
    if (this.onCameraMoveStartedListener) {
      this.onCameraMoveStartedListener.remove();
    }

    if (callback) {
      this.onCameraMoveStartedListener = CapacitorGoogleMaps.addListener(
        'onCameraMoveStarted',
        this.generateCallback(callback),
      );
    } else {
      this.onCameraMoveStartedListener = undefined;
    }
  }

  async setOnClusterClickListener(
    callback?: MapListenerCallback,
  ): Promise<void> {
    if (this.onClusterClickListener) {
      this.onClusterClickListener.remove();
    }

    if (callback) {
      this.onClusterClickListener = CapacitorGoogleMaps.addListener(
        'onClusterClick',
        this.generateCallback(callback),
      );
    } else {
      this.onClusterClickListener = undefined;
    }
  }

  async setOnClusterInfoWindowClickListener(
    callback?: MapListenerCallback,
  ): Promise<void> {
    if (this.onClusterInfoWindowClickListener) {
      this.onClusterInfoWindowClickListener.remove();
    }

    if (callback) {
      this.onClusterInfoWindowClickListener = CapacitorGoogleMaps.addListener(
        'onClusterInfoWindowClick',
        this.generateCallback(callback),
      );
    } else {
      this.onClusterInfoWindowClickListener = undefined;
    }
  }

  async setOnInfoWindowClickListener(
    callback?: MapListenerCallback,
  ): Promise<void> {
    if (this.onInfoWindowClickListener) {
      this.onInfoWindowClickListener.remove();
    }

    if (callback) {
      this.onInfoWindowClickListener = CapacitorGoogleMaps.addListener(
        'onInfoWindowClick',
        this.generateCallback(callback),
      );
    } else {
      this.onInfoWindowClickListener = undefined;
    }
  }

  async setOnMapClickListener(callback?: MapListenerCallback): Promise<void> {
    if (this.onMapClickListener) {
      this.onMapClickListener.remove();
    }

    if (callback) {
      this.onMapClickListener = CapacitorGoogleMaps.addListener(
        'onMapClick',
        this.generateCallback(callback),
      );
    } else {
      this.onMapClickListener = undefined;
    }
  }

  async setOnMarkerClickListener(
    callback?: MapListenerCallback,
  ): Promise<void> {
    if (this.onMarkerClickListener) {
      this.onMarkerClickListener.remove();
    }

    if (callback) {
      this.onMarkerClickListener = CapacitorGoogleMaps.addListener(
        'onMarkerClick',
        this.generateCallback(callback),
      );
    } else {
      this.onMarkerClickListener = undefined;
    }
  }

  async setOnMyLocationButtonClickListener(
    callback?: MapListenerCallback,
  ): Promise<void> {
    if (this.onMyLocationButtonClickListener) {
      this.onMyLocationButtonClickListener.remove();
    }

    if (callback) {
      this.onMyLocationButtonClickListener = CapacitorGoogleMaps.addListener(
        'onMyLocationButtonClick',
        this.generateCallback(callback),
      );
    } else {
      this.onMyLocationButtonClickListener = undefined;
    }
  }

  async setOnMyLocationClickListener(
    callback?: MapListenerCallback,
  ): Promise<void> {
    if (this.onMyLocationClickListener) {
      this.onMyLocationClickListener.remove();
    }

    if (callback) {
      this.onMyLocationClickListener = CapacitorGoogleMaps.addListener(
        'onMyLocationClick',
        this.generateCallback(callback),
      );
    } else {
      this.onMyLocationClickListener = undefined;
    }
  }

  private generateCallback(callback: MapListenerCallback): MapListenerCallback {
    const mapId = this.id;
    return (data: any) => {
      if (data.mapId == mapId) {
        callback(data);
      }
    };
  }

  async removeAllMapListeners(): Promise<void> {
    if (this.onCameraIdleListener) {
      this.onCameraIdleListener.remove();
      this.onCameraIdleListener = undefined;
    }
    if (this.onCameraMoveStartedListener) {
      this.onCameraMoveStartedListener.remove();
      this.onCameraMoveStartedListener = undefined;
    }

    if (this.onClusterClickListener) {
      this.onClusterClickListener.remove();
      this.onClusterClickListener = undefined;
    }

    if (this.onClusterInfoWindowClickListener) {
      this.onClusterInfoWindowClickListener.remove();
      this.onClusterInfoWindowClickListener = undefined;
    }

    if (this.onInfoWindowClickListener) {
      this.onInfoWindowClickListener.remove();
      this.onInfoWindowClickListener = undefined;
    }

    if (this.onMapClickListener) {
      this.onMapClickListener.remove();
      this.onMapClickListener = undefined;
    }

    if (this.onMarkerClickListener) {
      this.onMarkerClickListener.remove();
      this.onMarkerClickListener = undefined;
    }

    if (this.onMyLocationButtonClickListener) {
      this.onMyLocationButtonClickListener.remove();
      this.onMyLocationClickListener = undefined;
    }

    if (this.onMyLocationClickListener) {
      this.onMyLocationClickListener.remove();
      this.onMyLocationButtonClickListener = undefined;
    }
  }
}
