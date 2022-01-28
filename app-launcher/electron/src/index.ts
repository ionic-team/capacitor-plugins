import { shell } from 'electron';

import type {
  CanOpenURLOptions,
  CanOpenURLResult,
  OpenURLOptions,
  OpenURLResult,
} from '../../src/definitions';

import type { AppLauncherPlugin } from './definitions';

export class AppLauncher implements AppLauncherPlugin {
  private _protocols: string[] | false;

  constructor(config: Record<string, any>) {
    const protocols =
      config?.plugins?.['app-launcher']?.electron?.protocols ?? [];

    this._protocols = Array.isArray(protocols)
      ? protocols.map(s => s.replace(':', ''))
      : false;
  }

  canOpenUrl(options: CanOpenURLOptions): Promise<CanOpenURLResult> {
    const { url } = options ?? {};

    try {
      // Try parse URL
      new URL(url);

      // Electron does not support checking if the URL can be opened, but we assume it can
      return Promise.resolve({ value: true });
    } catch {
      return Promise.resolve({ value: false });
    }
  }

  openUrl(options: OpenURLOptions): Promise<OpenURLResult> {
    const { url, ...extraOptions } = options ?? {};

    const handleOpen = () => {
      // Electron does not return the status, so this is always completed
      return shell
        .openExternal(url, extraOptions)
        .then(() => ({ completed: true }));
    };

    try {
      const parsedUrl = new URL(url);

      if (!Array.isArray(this._protocols)) {
        console.warn(
          '\u001b[31mTurning off protocol verification can be dangerous!\u001b[0m: https://benjamin-altpeter.de/shell-openexternal-dangers/',
        );

        return handleOpen();
      }

      if (this._protocols.length < 1) {
        return Promise.reject(
          new Error(
            'No allowed protocols found in the Capacitor config file. See: <link to docs>',
          ),
        );
      }

      const protocol = parsedUrl.protocol.replace(':', '');

      if (!this._protocols.includes(protocol)) {
        return Promise.reject(new Error(`Protocol not allowed (${protocol})`));
      }

      return handleOpen();
    } catch (err) {
      return Promise.reject(err);
    }
  }
}
