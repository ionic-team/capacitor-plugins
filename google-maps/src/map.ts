import type { PluginListenerHandle } from '@capacitor/core';

import type { GoogleMapConfig, LatLng } from './definitions';
import type { MapListenerCallback } from './implementation';
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
  private onMapClickListener?: PluginListenerHandle;
  private onMarkerClickListener?: PluginListenerHandle;

  private constructor(id: string) {
    this.id = id;
  }
  public static async create(
    id: string,
    apiKey: string,
    config: GoogleMapConfig,
    forceCreate?: boolean,
    callback?: MapListenerCallback,
  ): Promise<GoogleMap> {
    const newMap = new GoogleMap(id);

    await CapacitorGoogleMaps.create({
      id,
      apiKey,
      config,
      forceCreate,
    });

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
    if (this.onMapClickListener) {
      this.onMapClickListener.remove();
    }

    if (this.onMarkerClickListener) {
      this.onMarkerClickListener.remove();
    }

    return CapacitorGoogleMaps.destroy({
      id: this.id,
    });
  }

  async setOnMapClickListener(callback?: MapListenerCallback): Promise<void> {
    if (this.onMapClickListener) {
      this.onMapClickListener.remove();
    }

    if (callback) {
      this.onMapClickListener = CapacitorGoogleMaps.addListener(
        'onMapClick',
        callback,
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
        callback,
      );
    } else {
      this.onMarkerClickListener = undefined;
    }
  }
}
