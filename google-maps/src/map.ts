import { Capacitor } from '@capacitor/core';
import type { PluginListenerHandle } from '@capacitor/core';

import type {
  CameraConfig,
  GoogleMapConfig,
  LatLng,
  MapPadding,
  MapType,
} from './definitions';
import type { CreateMapArgs, MapListenerCallback } from './implementation';
import { CapacitorGoogleMaps } from './implementation';

export interface GoogleMapInterface {
  create(
    element: HTMLElement,
    id: string,
    apiKey: string,
    config: GoogleMapConfig,
    forceCreate?: boolean,
  ): Promise<GoogleMap>;
  enableClustering(): Promise<void>;
  disableClustering(): Promise<void>;
  addMarker(marker: Marker): Promise<string>;
  addMarkers(markers: Marker[]): Promise<string[]>;
  removeMarker(id: string): Promise<void>;
  removeMarkers(ids: string[]): Promise<void>;
  destroy(): Promise<void>;
  setCamera(config: CameraConfig): Promise<void>;
  setMapType(mapType: MapType): Promise<void>;
  enableIndoorMaps(enabled: boolean): Promise<void>;
  enableTrafficLayer(enabled: boolean): Promise<void>;
  enableAccessibilityElements(enabled: boolean): Promise<void>;
  enableCurrentLocation(enabled: boolean): Promise<void>;
  setPadding(padding: MapPadding): Promise<void>;
  setOnCameraIdleListener(callback?: MapListenerCallback): Promise<void>;
  setOnCameraMoveStartedListener(callback?: MapListenerCallback): Promise<void>;
  setOnClusterClickListener(callback?: MapListenerCallback): Promise<void>;
  setOnClusterInfoWindowClickListener(
    callback?: MapListenerCallback,
  ): Promise<void>;
  setOnInfoWindowClickListener(callback?: MapListenerCallback): Promise<void>;
  setOnMapClickListener(callback?: MapListenerCallback): Promise<void>;
  setOnMarkerClickListener(callback?: MapListenerCallback): Promise<void>;
  setOnMyLocationButtonClickListener(
    callback?: MapListenerCallback,
  ): Promise<void>;
  setOnMyLocationClickListener(callback?: MapListenerCallback): Promise<void>;
}

/**
 * A marker is an icon placed at a particular point on the map's surface.
 */
export interface Marker {
  /**
   * Marker position
   */
  coordinate: LatLng;
  /**
   * Sets the opacity of the marker, between 0 (completely transparent) and 1 inclusive.
   *
   * @default 1
   */
  opacity?: number;
  /**
   * Title, a short description of the overlay.
   */
  title?: string;
  /**
   * Snippet text, shown beneath the title in the info window when selected.
   */
  snippet?: string;
  /**
   * Controls whether this marker should be flat against the Earth's surface or a billboard facing the camera.
   *
   * @default false
   */
  isFlat?: boolean;
  /**
   * Marker icon to render.
   */
  iconUrl?: string;
  /**
   * Controls whether this marker can be dragged interactively
   *
   * @default false
   */
  draggable?: boolean;
}

export class GoogleMap {
  private id: string;
  private element: HTMLElement | null = null;
  private frameElement: HTMLElement | null = null;
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

  /**
   * Creates a new instance of a Google Map
   *
   * @param element
   * DOM element that will contain the map view and determine sizing / positioning
   * @param id Unique id for the map instance
   * @param apiKey
   * Google Maps SDK API Key
   * @param config
   * Initial configuration settings for the map
   * @param forceCreate
   * If a map already exists with the supplied id, automatically destroy and re-create the map instance
   *
   * @returns GoogleMap
   */
  public static async create(
    element: HTMLElement,
    id: string,
    apiKey: string,
    config: GoogleMapConfig,
    forceCreate?: boolean,
    callback?: MapListenerCallback,
  ): Promise<GoogleMap> {
    const newMap = new GoogleMap(id);

    if (!element) {
      throw new Error('container element is required');
    }

    newMap.element = element;

    const elementBounds = element.getBoundingClientRect();
    config.width = elementBounds.width;
    config.height = elementBounds.height;
    config.x = elementBounds.x;
    config.y = elementBounds.y;

    const args: CreateMapArgs = {
      element,
      id,
      apiKey,
      config,
      forceCreate,
    };

    if (Capacitor.isNativePlatform()) {
      (args.element as any) = {};
      newMap.frameElement = newMap.findMapContainerElement();
      newMap.initScrolling();

      const frameRect = newMap.frameElement?.getBoundingClientRect();

      if (frameRect) {
        args.frame = {
          height: frameRect.height,
          width: frameRect.width,
          x: frameRect.x,
          y: frameRect.y,
        };
      }
    }

    await CapacitorGoogleMaps.create(args);

    if (callback) {
      CapacitorGoogleMaps.addListener('onMapReady', callback);
    }

    return newMap;
  }

  /**
   * Enable marker clustering
   *
   * @returns void
   */
  async enableClustering(): Promise<void> {
    return CapacitorGoogleMaps.enableClustering({
      id: this.id,
    });
  }

  /**
   * Disable marker clustering
   *
   * @returns void
   */
  async disableClustering(): Promise<void> {
    return CapacitorGoogleMaps.disableClustering({
      id: this.id,
    });
  }

  /**
   * Adds a marker to the map
   *
   * @param marker
   * @returns created marker id
   */
  async addMarker(marker: Marker): Promise<string> {
    const res = await CapacitorGoogleMaps.addMarker({
      id: this.id,
      marker,
    });

    return res.id;
  }

  /**
   * Adds multiple markers to the map
   *
   * @param markers
   * @returns array of created marker IDs
   */
  async addMarkers(markers: Marker[]): Promise<string[]> {
    const res = await CapacitorGoogleMaps.addMarkers({
      id: this.id,
      markers,
    });

    return res.ids;
  }

  /**
   * Remove marker from the map
   *
   * @param id id of the marker to remove from the map
   * @returns
   */
  async removeMarker(id: string): Promise<void> {
    return CapacitorGoogleMaps.removeMarker({
      id: this.id,
      markerId: id,
    });
  }

  /**
   * Remove markers from the map
   *
   * @param ids array of ids to remove from the map
   * @returns
   */
  async removeMarkers(ids: string[]): Promise<void> {
    return CapacitorGoogleMaps.removeMarkers({
      id: this.id,
      markerIds: ids,
    });
  }

  /**
   * Destroy the current instance of the map
   */
  async destroy(): Promise<void> {
    if (Capacitor.isNativePlatform()) {
      this.disableScrolling();
    }

    this.removeAllMapListeners();

    return CapacitorGoogleMaps.destroy({
      id: this.id,
    });
  }

  /**
   * Update the map camera configuration
   *
   * @param config
   * @returns
   */
  async setCamera(config: CameraConfig): Promise<void> {
    return CapacitorGoogleMaps.setCamera({
      id: this.id,
      config,
    });
  }

  /**
   * Sets the type of map tiles that should be displayed.
   *
   * @param mapType
   * @returns
   */
  async setMapType(mapType: MapType): Promise<void> {
    return CapacitorGoogleMaps.setMapType({
      id: this.id,
      mapType,
    });
  }

  /**
   * Sets whether indoor maps are shown, where available.
   *
   * @param enabled
   * @returns
   */
  async enableIndoorMaps(enabled: boolean): Promise<void> {
    return CapacitorGoogleMaps.enableIndoorMaps({
      id: this.id,
      enabled,
    });
  }

  /**
   * Controls whether the map is drawing traffic data, if available.
   *
   * @param enabled
   * @returns
   */
  async enableTrafficLayer(enabled: boolean): Promise<void> {
    return CapacitorGoogleMaps.enableTrafficLayer({
      id: this.id,
      enabled,
    });
  }

  /**
   * Show accessibility elements for overlay objects, such as Marker and Polyline.
   *
   * Only available on iOS.
   *
   * @param enabled
   * @returns
   */
  async enableAccessibilityElements(enabled: boolean): Promise<void> {
    return CapacitorGoogleMaps.enableAccessibilityElements({
      id: this.id,
      enabled,
    });
  }

  /**
   * Set whether the My Location dot and accuracy circle is enabled.
   *
   * @param enabled
   * @returns
   */
  async enableCurrentLocation(enabled: boolean): Promise<void> {
    return CapacitorGoogleMaps.enableCurrentLocation({
      id: this.id,
      enabled,
    });
  }

  /**
   * Set padding on the 'visible' region of the view.
   *
   * @param padding
   * @returns
   */
  async setPadding(padding: MapPadding): Promise<void> {
    return CapacitorGoogleMaps.setPadding({
      id: this.id,
      padding,
    });
  }

  /**
   * Set the event listener on the map for 'onCameraIdle' events.
   *
   * @param callback
   * @returns
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

  /**
   * Set the event listener on the map for 'onCameraMoveStarted' events.
   *
   * @param callback
   * @returns
   */
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

  /**
   * Set the event listener on the map for 'onClusterClick' events.
   *
   * @param callback
   * @returns
   */
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

  /**
   * Set the event listener on the map for 'onClusterInfoWindowClick' events.
   *
   * @param callback
   * @returns
   */
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

  /**
   * Set the event listener on the map for 'onInfoWindowClick' events.
   *
   * @param callback
   * @returns
   */
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

  /**
   * Set the event listener on the map for 'onMapClick' events.
   *
   * @param callback
   * @returns
   */
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

  /**
   * Set the event listener on the map for 'onMarkerClick' events.
   *
   * @param callback
   * @returns
   */
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

  /**
   * Set the event listener on the map for 'onMyLocationButtonClick' events.
   *
   * @param callback
   * @returns
   */
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

  /**
   * Set the event listener on the map for 'onMyLocationClick' events.
   *
   * @param callback
   * @returns
   */
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

  /**
   * Remove all event listeners on the map.
   *
   * @param callback
   * @returns
   */
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

  private initScrolling(): void {
    if (this.frameElement) {
      let scrollEvent = 'scroll';

      if (this.frameElement.tagName.toLowerCase() == 'ion-content') {
        (this.frameElement as any).scrollEvents = true;
        scrollEvent = 'ionScroll';
      }

      window.addEventListener(scrollEvent, this.handleScrollEvent);
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
    } else {
      console.warn('An appropriate map container element has not been found');
    }
  }

  private disableScrolling(): void {
    if (this.frameElement) {
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
  }

  private handleScrollEvent = (): void => this.updateMapBounds();

  private updateMapBounds(): void {
    if (this.element && this.frameElement) {
      const mapRect = this.element.getBoundingClientRect();
      const frameRect = this.frameElement.getBoundingClientRect();

      CapacitorGoogleMaps.onScroll({
        id: this.id,
        frame: {
          x: frameRect.x,
          y: frameRect.y,
          width: frameRect.width,
          height: frameRect.height,
        },
        mapBounds: {
          x: mapRect.x,
          y: mapRect.y,
          width: mapRect.width,
          height: mapRect.height,
        },
      });
    }
  }

  private findMapContainerElement(): HTMLElement | null {
    if (!this.element) {
      return null;
    }

    let parentElement = this.element.parentElement;
    while (parentElement !== null) {
      if (parentElement.classList.contains('capacitorGoogleMapContainer')) {
        return parentElement;
      }

      parentElement = parentElement.parentElement;
    }

    return null;
  }

  private generateCallback(callback: MapListenerCallback): MapListenerCallback {
    const mapId = this.id;
    return (data: any) => {
      if (data.mapId == mapId) {
        callback(data);
      }
    };
  }
}
