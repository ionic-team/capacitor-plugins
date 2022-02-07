import type { GoogleMapConfig } from './definitions';
import { CapacitorGoogleMaps } from './implementation';

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

  async destroy(): Promise<void> {
    return CapacitorGoogleMaps.destroy({
      id: this.id,
    });
  }
}
