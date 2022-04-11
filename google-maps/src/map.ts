import { Capacitor } from '@capacitor/core';

import type {
  CameraConfig,
  GoogleMapConfig,
  LatLng,
  MapPadding,
  MapType,
} from './definitions';
import type { CreateMapArgs } from './implementation';
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
    //@ts-ignore
    this.style['-webkit-overflow-scrolling'] = 'touch';

    const overflowDiv = document.createElement('div');
    overflowDiv.style.height = '200%';

    this.appendChild(overflowDiv);
  }
}

customElements.define('capacitor-google-map', MapCustomElement);

export class GoogleMap {
  private id: string;
  private element: HTMLElement | null = null;

  private constructor(id: string) {
    this.id = id;
  }

  public static async create(
    element: HTMLElement,
    id: string,
    apiKey: string,
    config: GoogleMapConfig,
    forceCreate?: boolean,
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

    if (Capacitor.getPlatform() == 'android') {
      (args.element as any) = {};
      newMap.initScrolling();
    }

    if (Capacitor.getPlatform() == 'ios') {
      (args.element as any) = {};
    }

    await CapacitorGoogleMaps.create(args);

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
}
