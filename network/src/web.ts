import type { PluginListenerHandle } from '@capacitor/core';
import { WebPlugin } from '@capacitor/core';

import type {
  ConnectionStatusChangeListener,
  ConnectionStatus,
  ConnectionType,
  NetworkPlugin,
} from './definitions';

declare global {
  interface Navigator {
    connection: any;
    mozConnection: any;
    webkitConnection: any;
  }
}

function translatedConnection(): ConnectionType {
  const connection =
    window.navigator.connection ||
    window.navigator.mozConnection ||
    window.navigator.webkitConnection;
  let result: ConnectionType = 'unknown';
  const type = connection ? connection.type || connection.effectiveType : null;
  if (type && typeof type === 'string') {
    switch (type) {
      // possible type values
      case 'bluetooth':
      case 'cellular':
        result = 'cellular';
        break;
      case 'none':
        result = 'none';
        break;
      case 'ethernet':
      case 'wifi':
      case 'wimax':
        result = 'wifi';
        break;
      case 'other':
      case 'unknown':
        result = 'unknown';
        break;
      // possible effectiveType values
      case 'slow-2g':
      case '2g':
      case '3g':
        result = 'cellular';
        break;
      case '4g':
        result = 'wifi';
        break;
      default:
        break;
    }
  }
  return result;
}

export class NetworkWeb extends WebPlugin implements NetworkPlugin {
  async getStatus(): Promise<ConnectionStatus> {
    if (!window.navigator) {
      throw this.unavailable(
        'Browser does not support the Network Information API',
      );
    }

    const connected = window.navigator.onLine;
    const connectionType = translatedConnection();

    const status: ConnectionStatus = {
      connected: connected,
      connectionType: connected ? connectionType : 'none',
    };

    return status;
  }

  addListener(
    eventName: 'networkStatusChange',
    listenerFunc: ConnectionStatusChangeListener,
  ): PluginListenerHandle {
    const thisRef = this;
    const connectionType = translatedConnection();

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
        remove: () => {
          /* do nothing */
        },
      };
    }
  }
}

const Network = new NetworkWeb();

export { Network };
