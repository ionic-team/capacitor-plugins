import { UnsupportedBrowserException, WebPlugin } from '@capacitor/core';

import {
  GeolocationPlugin,
  GeolocationOptions,
  GeolocationPosition,
  GeolocationWatchCallback,
  GeolocationPermissionStatus,
} from './definitions';

import { extend } from './util';

export class GeolocationWeb extends WebPlugin implements GeolocationPlugin {
  constructor() {
    super({ name: 'Geolocation' });
  }

  async getCurrentPosition(
    options?: GeolocationOptions,
  ): Promise<GeolocationPosition> {
    return new Promise((resolve, reject) => {
      navigator.geolocation.getCurrentPosition(
        pos => {
          resolve(pos);
        },
        err => {
          reject(err);
        },
        extend(
          {
            enableHighAccuracy: true,
            timeout: 10000,
            maximumAge: 0,
          },
          options,
        ),
      );
    });
  }

  watchPosition(
    options: GeolocationOptions,
    callback: GeolocationWatchCallback,
  ): string {
    let id = navigator.geolocation.watchPosition(
      pos => {
        callback(pos);
      },
      err => {
        callback(null, err);
      },
      extend(
        {
          enableHighAccuracy: true,
          timeout: 10000,
          maximumAge: 0,
        },
        options,
      ),
    );

    return `${id}`;
  }

  clearWatch(options: { id: string }) {
    window.navigator.geolocation.clearWatch(parseInt(options.id, 10));
    return Promise.resolve();
  }

  async checkPermissions(): Promise<GeolocationPermissionStatus> {
    if (typeof navigator === 'undefined' || !navigator.permissions) {
      throw new UnsupportedBrowserException(
        'Permissions API not available in this browser',
      );
    }

    const permission = await window.navigator.permissions.query({
      name: 'geolocation',
    });
    return Promise.resolve({ location: permission.state });
  }

  requestPermissions(): Promise<GeolocationPermissionStatus> {
    return Promise.resolve({ location: 'granted' });
  }
}

const Geolocation = new GeolocationWeb();

export { Geolocation };
