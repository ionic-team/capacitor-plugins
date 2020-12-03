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

  parseUa(ua: string): any {
    const uaFields: any = {};
    const start = ua.indexOf('(') + 1;
    let end = ua.indexOf(') AppleWebKit');
    if (ua.indexOf(') Gecko') !== -1) {
      end = ua.indexOf(') Gecko');
    }
    const fields = ua.substring(start, end);
    if (ua.indexOf('Android') !== -1) {
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
        if (ua.indexOf('Windows') !== -1) {
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

    if (/android/i.test(ua)) {
      uaFields.operatingSystem = 'android';
    } else if (/iPad|iPhone|iPod/.test(ua) && !window.MSStream) {
      uaFields.operatingSystem = 'ios';
    } else if (/Win/.test(ua)) {
      uaFields.operatingSystem = 'windows';
    } else if (/Mac/i.test(ua)) {
      uaFields.operatingSystem = 'mac';
    } else {
      uaFields.operatingSystem = 'unknown';
    }

    // Check for browsers based on non-standard javascript apis, not user agent
    const isFirefox = !!(window as any).InstallTrigger;
    const isSafari = !!(window as any).ApplePaySession;
    const isChrome = !!(window as any).chrome;
    const isEdge = /Edge/.test(navigator.userAgent);

    // FF and Edge User Agents both end with "/MAJOR.MINOR"
    if (isFirefox || isEdge) {
      const reverseUA = ua.split('').reverse().join('');
      const reverseVersion = reverseUA.split('/')[0];
      const version = reverseVersion.split('').reverse().join('');
      uaFields.browserVersion = version;
    } else if (isSafari || isChrome) {
      // Safari version comes after "... Version/MAJOR.MINOR ...""
      // Chrome version comes after "... Chrome/MAJOR.MINOR ...""
      const searchWord = isChrome ? 'Chrome' : 'Version';
      const words = ua.split(' ');
      words.forEach(word => {
        if (word.includes(searchWord)) {
          const version = word.split('/')[1];
          uaFields.browserVersion = version;
        }
      });
    }

    return uaFields;
  }

  getUid(): string {
    if (typeof window !== 'undefined') {
      let uid = window.localStorage.getItem('_capuid');
      if (uid) {
        return uid;
      }

      uid = this.uuid4();
      window.localStorage.setItem('_capuid', uid);
      return uid;
    }
    return this.uuid4();
  }

  uuid4(): string {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(
      /[xy]/g,
      function (c) {
        const r = (Math.random() * 16) | 0,
          v = c === 'x' ? r : (r & 0x3) | 0x8;
        return v.toString(16);
      },
    );
  }
}
