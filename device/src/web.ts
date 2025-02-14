import { WebPlugin } from '@capacitor/core';

import type {
  BatteryInfo,
  DeviceId,
  DeviceInfo,
  DevicePlugin,
  GetLanguageCodeResult,
  LanguageTag,
} from './definitions';

declare global {
  interface Navigator {
    getBattery: any;
    oscpu: any;
  }

  interface Window {
    InstallTrigger?: any;
    ApplePaySession?: any;
    chrome?: any;
  }
}

export class DeviceWeb extends WebPlugin implements DevicePlugin {
  async getId(): Promise<DeviceId> {
    return {
      identifier: this.getUid(),
    };
  }

  async getInfo(): Promise<DeviceInfo> {
    if (typeof navigator === 'undefined' || !navigator.userAgent) {
      throw this.unavailable('Device API not available in this browser');
    }

    const ua = navigator.userAgent;
    const uaFields = this.parseUa(ua);

    return {
      model: uaFields.model,
      platform: 'web' as const,
      operatingSystem: uaFields.operatingSystem,
      osVersion: uaFields.osVersion,
      manufacturer: navigator.vendor,
      isVirtual: false,
      webViewVersion: uaFields.browserVersion,
    };
  }

  async getBatteryInfo(): Promise<BatteryInfo> {
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

  async getLanguageCode(): Promise<GetLanguageCodeResult> {
    return {
      value: navigator.language.split('-')[0].toLowerCase(),
    };
  }

  async getLanguageTag(): Promise<LanguageTag> {
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

    // Check for browsers based on non-standard javascript apis, only not user agent
    const isSafari = !!window.ApplePaySession;
    const isChrome = !!window.chrome;
    const isFirefox = /Firefox/.test(ua);
    const isEdge = /Edg/.test(ua);
    const isFirefoxIOS = /FxiOS/.test(ua);
    const isChromeIOS = /CriOS/.test(ua);
    const isEdgeIOS = /EdgiOS/.test(ua);

    // FF and Edge User Agents both end with "/MAJOR.MINOR"
    if (
      isSafari ||
      (isChrome && !isEdge) ||
      isFirefoxIOS ||
      isChromeIOS ||
      isEdgeIOS
    ) {
      // Safari version comes as     "... Version/MAJOR.MINOR ..."
      // Chrome version comes as     "... Chrome/MAJOR.MINOR ..."
      // FirefoxIOS version comes as "... FxiOS/MAJOR.MINOR ..."
      // ChromeIOS version comes as  "... CriOS/MAJOR.MINOR ..."
      let searchWord: string;
      if (isFirefoxIOS) {
        searchWord = 'FxiOS';
      } else if (isChromeIOS) {
        searchWord = 'CriOS';
      } else if (isEdgeIOS) {
        searchWord = 'EdgiOS';
      } else if (isSafari) {
        searchWord = 'Version';
      } else {
        searchWord = 'Chrome';
      }

      const words = ua.split(' ');
      for (const word of words) {
        if (word.includes(searchWord)) {
          const version = word.split('/')[1];
          uaFields.browserVersion = version;
        }
      }
    } else if (isFirefox || isEdge) {
      const reverseUA = ua.split('').reverse().join('');
      const reverseVersion = reverseUA.split('/')[0];
      const version = reverseVersion.split('').reverse().join('');
      uaFields.browserVersion = version;
    }

    return uaFields;
  }

  getUid(): string {
    if (typeof window !== 'undefined' && window.localStorage) {
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
