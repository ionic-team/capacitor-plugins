import type { GoogleMapConfig, LatLng } from './definitions';
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

    const elementBounds = element.getBoundingClientRect();
    const coordsAndSize = {
      x: elementBounds.x,
      y: elementBounds.y,
      width: elementBounds.width,
      height: elementBounds.height,
    };

    await CapacitorGoogleMaps.create({
      element,
      id,
      apiKey,
      config: { ...config, ...coordsAndSize },
      forceCreate,
    });

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
    return CapacitorGoogleMaps.destroy({
      id: this.id,
    });
  }
}
