import { WebPlugin } from '@capacitor/core';

import type { ConnectionStatus, ConnectionType, NetworkPlugin } from './definitions';

declare global {
  interface Navigator {
    connection: any;
    mozConnection: any;
    webkitConnection: any;
  }
}

function translatedConnection(): ConnectionType {
  const connection = window.navigator.connection || window.navigator.mozConnection || window.navigator.webkitConnection;
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
  constructor() {
    super();
    if (typeof window !== 'undefined') {
      window.addEventListener('online', this.handleOnline);
      window.addEventListener('offline', this.handleOffline);
    }
  }

  async getStatus(): Promise<ConnectionStatus> {
    if (!window.navigator) {
      throw this.unavailable('Browser does not support the Network Information API');
    }

    const connected = window.navigator.onLine;
    const connectionType = translatedConnection();

    const status: ConnectionStatus = {
      connected,
      connectionType: connected ? connectionType : 'none',
    };

    return status;
  }

  private handleOnline = () => {
    const connectionType = translatedConnection();

    const status: ConnectionStatus = {
      connected: true,
      connectionType: connectionType,
    };

    this.notifyListeners('networkStatusChange', status);
  };

  private handleOffline = () => {
    const status: ConnectionStatus = {
      connected: false,
      connectionType: 'none',
    };

    this.notifyListeners('networkStatusChange', status);
  };
}

const Network = new NetworkWeb();

export { Network };
