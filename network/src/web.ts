import {
  UnsupportedBrowserException,
  WebPlugin,
  PluginListenerHandle,
} from '@capacitor/core';
import { NetworkPlugin, NetworkStatus } from './definitions';

declare var window: any;

export class NetworkWeb extends WebPlugin implements NetworkPlugin {
  constructor() {
    super({ name: 'Network' });
  }

  async getStatus(): Promise<NetworkStatus> {
    if (!window.navigator) {
      throw new UnsupportedBrowserException(
        'Browser does not support the Network Information API',
      );
    }

    const connected = window.navigator.onLine;
    const connection =
      window.navigator.connection ||
      window.navigator.mozConnection ||
      window.navigator.webkitConnection;
    const connectionType = connection
      ? connection.type || connection.effectiveType
      : 'wifi';

    const status: NetworkStatus = {
      connected: connected,
      connectionType: connected ? connectionType : 'none',
    };

    return status;
  }

  addListener(
    eventName: 'networkStatusChange',
    listenerFunc: (status: NetworkStatus) => void,
  ): PluginListenerHandle {
    const thisRef = this;
    const connection =
      window.navigator.connection ||
      window.navigator.mozConnection ||
      window.navigator.webkitConnection;
    const connectionType = connection
      ? connection.type || connection.effectiveType
      : 'wifi';

    const onlineBindFunc = listenerFunc.bind(thisRef, {
      connected: true,
      connectionType: connectionType,
    });
    const offlineBindFunc = listenerFunc.bind(thisRef, {
      connected: false,
      connectionType: 'none',
    });

    if (eventName.localeCompare('networkStatusChange') === 0) {
      window.addEventListener('online', onlineBindFunc);
      window.addEventListener('offline', offlineBindFunc);
      return {
        remove: () => {
          window.removeEventListener('online', onlineBindFunc);
          window.removeEventListener('offline', offlineBindFunc);
        },
      };
    } else {
      return {
        remove: () => {},
      };
    }
  }
}

const Network = new NetworkWeb();

export { Network };
