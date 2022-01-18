import { CapacitorGoogleMaps } from './implementation';

export interface MapOptions {
  width: number;
  height: number;
  x: number;
  y: number;
  lat: number;
  lng: number;
  androidLiteMode: boolean;
}

export class Map {
  public id: string;

  constructor(id: string, options: MapOptions) {
    this.id = id;
    CapacitorGoogleMaps.create(this.id, options).catch(err => {
      throw err;
    });
  }

  async destroy(): Promise<void> {
    CapacitorGoogleMaps.destroy(this.id);
  }
}
