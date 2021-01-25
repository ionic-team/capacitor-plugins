import { WebPlugin, CapacitorException } from '@capacitor/core';

import type {
  CameraPlugin,
  ImageOptions,
  PermissionStatus,
  Photo,
} from './definitions';
import { CameraSource, CameraDirection } from './definitions';

export class CameraWeb extends WebPlugin implements CameraPlugin {
  async getPhoto(options: ImageOptions): Promise<Photo> {
    // eslint-disable-next-line no-async-promise-executor
    return new Promise<Photo>(async (resolve, reject) => {
      if (options.webUseInput) {
        this.fileInputExperience(options, resolve);
      } else {
        if (customElements.get('pwa-camera-modal')) {
          const cameraModal: any = document.createElement('pwa-camera-modal');
          document.body.appendChild(cameraModal);
          try {
            await cameraModal.componentOnReady();
            cameraModal.addEventListener('onPhoto', async (e: any) => {
              const photo = e.detail;

              if (photo === null) {
                reject(new CapacitorException('User cancelled photos app'));
              } else if (photo instanceof Error) {
                reject(photo);
              } else {
                resolve(await this._getCameraPhoto(photo, options));
              }

              cameraModal.dismiss();
              document.body.removeChild(cameraModal);
            });

            cameraModal.present();
          } catch (e) {
            this.fileInputExperience(options, resolve);
          }
        } else {
          console.error(
            `Unable to load PWA Element 'pwa-camera-modal'. See the docs: https://capacitorjs.com/docs/pwa-elements.`,
          );
          this.fileInputExperience(options, resolve);
        }
      }
    });
  }

  private fileInputExperience(options: ImageOptions, resolve: any) {
    let input = document.querySelector(
      '#_capacitor-camera-input',
    ) as HTMLInputElement;

    const cleanup = () => {
      input.parentNode?.removeChild(input);
    };

    if (!input) {
      input = document.createElement('input') as HTMLInputElement;
      input.id = '_capacitor-camera-input';
      input.type = 'file';
      document.body.appendChild(input);
    }

    input.accept = 'image/*';
    (input as any).capture = true;

    if (
      options.source === CameraSource.Photos ||
      options.source === CameraSource.Prompt
    ) {
      input.removeAttribute('capture');
    } else if (options.direction === CameraDirection.Front) {
      (input as any).capture = 'user';
    } else if (options.direction === CameraDirection.Rear) {
      (input as any).capture = 'environment';
    }

    input.addEventListener('change', (_e: any) => {
      const file = input.files![0];
      let format = 'jpeg';

      if (file.type === 'image/png') {
        format = 'png';
      } else if (file.type === 'image/gif') {
        format = 'gif';
      }

      if (options.resultType === 'dataUrl' || options.resultType === 'base64') {
        const reader = new FileReader();

        reader.addEventListener('load', () => {
          if (options.resultType === 'dataUrl') {
            resolve({
              dataUrl: reader.result,
              format,
            } as Photo);
          } else if (options.resultType === 'base64') {
            const b64 = (reader.result as string).split(',')[1];
            resolve({
              base64String: b64,
              format,
            } as Photo);
          }

          cleanup();
        });

        reader.readAsDataURL(file);
      } else {
        resolve({
          webPath: URL.createObjectURL(file),
          format: format,
        });
        cleanup();
      }
    });

    input.click();
  }

  private _getCameraPhoto(photo: Blob, options: ImageOptions) {
    return new Promise<Photo>((resolve, reject) => {
      const reader = new FileReader();
      const format = photo.type.split('/')[1];
      if (options.resultType === 'uri') {
        resolve({
          webPath: URL.createObjectURL(photo),
          format: format,
        });
      } else {
        reader.readAsDataURL(photo);
        reader.onloadend = () => {
          const r = reader.result as string;
          if (options.resultType === 'dataUrl') {
            resolve({
              dataUrl: r,
              format: format,
            });
          } else {
            resolve({
              base64String: r.split(',')[1],
              format: format,
            });
          }
        };
        reader.onerror = e => {
          reject(e);
        };
      }
    });
  }

  async checkPermissions(): Promise<PermissionStatus> {
    if (typeof navigator === 'undefined' || !navigator.permissions) {
      throw this.unavailable('Permissions API not available in this browser');
    }

    try {
      // https://developer.mozilla.org/en-US/docs/Web/API/Permissions/query
      // the specific permissions that are supported varies among browsers that implement the
      // permissions API, so we need a try/catch in case 'camera' is invalid
      const permission = await window.navigator.permissions.query({
        name: 'camera',
      });
      return {
        camera: permission.state,
        photos: 'granted',
      };
    } catch {
      throw this.unavailable(
        'Camera permissions are not available in this browser',
      );
    }
  }

  async requestPermissions(): Promise<PermissionStatus> {
    throw this.unimplemented('Not implemented on web.');
  }
}

const Camera = new CameraWeb();

export { Camera };
