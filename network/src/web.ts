import { WebPlugin, PluginListenerHandle } from '@capacitor/core';
import { NetworkPlugin, NetworkStatus } from './definitions';

declare var window: any;

export class NetworkWeb extends WebPlugin implements NetworkPlugin {
  listenerFunction: any = null;

  constructor() {
    super({ name: 'Network' });
  }

  getStatus(): Promise<NetworkStatus> {
    return new Promise((resolve, reject) => {
      if (!window.navigator) {
        reject('Network info not available');
        return;
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
      }

      resolve(status);
    });
  }

  addListener(
    eventName: 'networkStatusChange',
    listenerFunc: (status: NetworkStatus) => void,
  ): PluginListenerHandle {
    let thisRef = this;
    let connection =
      window.navigator.connection ||
      window.navigator.mozConnection ||
      window.navigator.webkitConnection;
    let connectionType = connection
      ? connection.type || connection.effectiveType
      : 'wifi';

    let onlineBindFunc = listenerFunc.bind(thisRef, {
      connected: true,
      connectionType: connectionType,
    });
    let offlineBindFunc = listenerFunc.bind(thisRef, {
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
