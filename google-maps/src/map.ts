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
    id: string,
    apiKey: string,
    config: GoogleMapConfig,
    forceCreate?: boolean,
  ): Promise<GoogleMap> {
    const newMap = new GoogleMap(id);

    await CapacitorGoogleMaps.create({
      id,
      apiKey,
      config,
      forceCreate,
    });

    return newMap;
  }

  async addMarker(marker: Marker): Promise<number>{
    return CapacitorGoogleMaps.addMarker({
      id: this.id,
      marker,
    });
  }

  async removeMarker(id: number): Promise<void> {
    return CapacitorGoogleMaps.removeMarker({
      id: this.id,
      markerId: id
    })
  }

  async destroy(): Promise<void> {
    return CapacitorGoogleMaps.destroy({
      id: this.id,
    });
  }
}
