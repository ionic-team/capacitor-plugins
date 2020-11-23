import { WebPlugin } from '@capacitor/core';

import type {
  DeviceBatteryInfo,
  DeviceInfo,
  DeviceLanguageCodeResult,
  DevicePlugin,
} from './definitions';

declare global {
  interface Navigator {
    getBattery: any;
    oscpu: any;
  }
}

export class DeviceWeb extends WebPlugin implements DevicePlugin {
  constructor() {
    super({ name: 'Device' });
  }

  async getInfo(): Promise<DeviceInfo> {
    if (typeof navigator === 'undefined' || !navigator.userAgent) {
      throw this.unavailable('Device API not available in this browser');
    }

    const ua = navigator.userAgent;
    const uaFields = this.parseUa(ua);

    return {
      model: uaFields.model,
      platform: <const>'web',
      operatingSystem: uaFields.operatingSystem,
      osVersion: uaFields.osVersion,
      manufacturer: navigator.vendor,
      isVirtual: false,
      uuid: this.getUid(),
      webViewVersion: uaFields.browserVersion,
      browserEngine: uaFields.browserEngine,
    };
  }

  async getBatteryInfo(): Promise<DeviceBatteryInfo> {
    if (typeof navigator === 'undefined' || !navigator.getBattery) {
      throw this.unavailable('Device API not available in this browser');
    }
    let battery: any = {};

    try {
      battery = await navigator.getBattery();
    } catch (e) {
      // Let it fail, we don't care
    }

    return {
      batteryLevel: battery.level,
      isCharging: battery.charging,
    };
  }

  async getLanguageCode(): Promise<DeviceLanguageCodeResult> {
    return {
      value: navigator.language,
    };
  }

  parseUa(_ua: string): any {
    const uaFields: any = {};
    const start = _ua.indexOf('(') + 1;
    let end = _ua.indexOf(') AppleWebKit');
    if (_ua.indexOf(') Gecko') !== -1) {
      end = _ua.indexOf(') Gecko');
    }
    const fields = _ua.substring(start, end);
    if (_ua.indexOf('Android') !== -1) {
      const tmpFields = fields.replace('; wv', '').split('; ').pop();
      if (tmpFields) {
        uaFields.model = tmpFields.split(' Build')[0];
      }
      uaFields.osVersion = fields.split('; ')[1];
    } else {
      uaFields.model = fields.split('; ')[0];
      if (typeof navigator !== 'undefined' && navigator.oscpu) {
        uaFields.osVersion = navigator.oscpu;
      } else {
        if (_ua.indexOf('Windows') !== -1) {
          uaFields.osVersion = fields;
        } else {
          const tmpFields = fields.split('; ').pop();
          if (tmpFields) {
            const lastParts = tmpFields
              .replace(' like Mac OS X', '')
              .split(' ');
            uaFields.osVersion = lastParts[lastParts.length - 1].replace(
              /_/g,
              '.',
            );
          }
        }
      }
    }

    if (/android/i.test(_ua)) {
      uaFields.operatingSystem = 'android';
    } else if (/iPad|iPhone|iPod/.test(_ua) && !window.MSStream) {
      uaFields.operatingSystem = 'ios';
    } else if (/Win/.test(_ua)) {
      uaFields.operatingSystem = 'windows';
    } else if (/Mac/i.test(_ua)) {
      uaFields.operatingSystem = 'mac';
    } else {
      uaFields.operatingSystem = 'unknown';
    }

    let match = _ua.match(/(opera|chrome|safari|firefox|msie|trident(?=\/))\/?\s*(\d+)/i) || [];
    let versions: RegExpMatchArray | RegExpExecArray | null;
    if (/trident/i.test(match[1])){
      versions =/\brv[ :]+(\d+)/g.exec(_ua) || [];
      uaFields.browserEngine = 'Internet Explorer';
      uaFields.browserVersion = versions[1] || '';
    } else if (match[1] === 'Chrome') {
      versions = _ua.match(/\bOPR|Edge\/(\d+)/)
      if(versions !== null) {
        uaFields.browserEngine = 'Opera';
        uaFields.browserVersion = versions[1] || '';
      }
    } else {
      match = match[2] ? [match[1], match[2]]: [navigator.appName, navigator.appVersion, '-?'];
      versions = _ua.match(/version\/(\d+)/i);
      if(versions !== null) {
        match.splice(1, 1, versions[1]);
      }
      uaFields.browserEngine =  match[0];
      uaFields.browserVersion = match[1];
    }

    return uaFields;
  }

  getUid(): string {
    let uid = window.localStorage.getItem('_capuid');
    if (uid) {
      return uid;
    }

    uid = this.uuid4();
    window.localStorage.setItem('_capuid', uid);
    return uid;
  }

  uuid4(): string {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (
      c,
    ) {
      const r = (Math.random() * 16) | 0,
        v = c === 'x' ? r : (r & 0x3) | 0x8;
      return v.toString(16);
    });
  }
}
