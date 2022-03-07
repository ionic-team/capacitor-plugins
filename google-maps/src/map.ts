import type {
  CameraConfig,
  GoogleMapConfig,
  LatLng,
  MapPadding,
  MapType,
} from './definitions';
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

export class GoogleMap {
  private id: string;
  private element: HTMLElement | null = null;
  private frameElement: HTMLElement | null = null;

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

    if (element) {
      newMap.element = element;

      const elementBounds = element.getBoundingClientRect();
      config.width = elementBounds.width;
      config.height = elementBounds.height;
      config.x = elementBounds.x;
      config.y = elementBounds.y;

      newMap.frameElement = newMap.findMapContainerElement();
    }

    await CapacitorGoogleMaps.create({
      id,
      apiKey,
      config,
      forceCreate,
    });

    await newMap.initScrolling();

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
    await this.disableScrolling();
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

  async initScrolling(): Promise<void> {
    if (this.frameElement) {
      console.log(this.frameElement.tagName.toLowerCase());

      if (this.frameElement.tagName.toLowerCase() == 'ion-content') {
        //@ts-ignore
        this.frameElement.scrollEvents = true;
        this.frameElement.addEventListener('ionScroll', () => {
          console.log('element is scrolling');
          this.updateMapBounds();
        });
        console.log('ionScroll Event Listener Setup');
      } else {
        this.frameElement.addEventListener('scroll', () => {
          console.log('element is scrolling');
          this.updateMapBounds();
        });
      }
    } else {
      console.warn('An appropriate map container element has not been found');
    }
  }

  async disableScrolling(): Promise<void> {
    if (this.frameElement) {
      this.frameElement.removeEventListener("ionScroll", () => {})
      this.frameElement.removeEventListener("scroll", () => {})
    }
  }

  private updateMapBounds() {
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
      if (parentElement.classList.contains('mapContainer')) {
        console.log('Parent Element Found');
        return parentElement;
      }

      parentElement = parentElement.parentElement;
    }

    return null;
  }
}
