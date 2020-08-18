import { WebPlugin, UnsupportedBrowserException } from '@capacitor/core';
import {
  ClipboardPlugin,
  ClipboardWriteOptions,
  ClipboardReadResult,
} from './definitions';

declare var ClipboardItem: any;

export class ClipboardWeb extends WebPlugin implements ClipboardPlugin {
  constructor() {
    super({
      name: 'Clipboard',
    });
  }

  async write(options: ClipboardWriteOptions): Promise<void> {
    if (!('clipboard' in navigator)) {
      throw new UnsupportedBrowserException(
        'Clipboard API not available in this browser',
      );
    }

    if (options.string !== undefined) {
      await this.writeText(options.string);
    } else if (options.url) {
      await this.writeText(options.url);
    } else if (options.image) {
      if ('ClipboardItem' in window && 'write' in navigator.clipboard) {
        try {
          const blob = await (await fetch(options.image)).blob();
          const clipboardItemInput = new ClipboardItem({ [blob.type]: blob });
          await (navigator.clipboard as any).write([clipboardItemInput]);
        } catch (err) {
          throw new Error('Failed to write image');
        }
      } else {
        throw new UnsupportedBrowserException(
          'Writing images to the clipboard is not supported in this browser',
        );
      }
    } else {
      throw new Error('Nothing to write');
    }
  }

  async read(): Promise<ClipboardReadResult> {
    if (!('clipboard' in navigator)) {
      throw new UnsupportedBrowserException(
        'Clipboard API not available in this browser',
      );
    }

    if ('ClipboardItem' in window && 'read' in navigator.clipboard) {
      try {
        const clipboardItems = await (navigator.clipboard as any).read();
        const type = clipboardItems[0].types[0];
        const clipboardBlob = await clipboardItems[0].getType(type);
        const data = await this._getBlobData(clipboardBlob, type);
        return { value: data, type };
      } catch (err) {
        return this.readText();
      }
    } else {
      return this.readText();
    }
  }

  private async readText() {
    if (!('readText' in navigator.clipboard)) {
      throw new Error('Reading from clipboard not supported in this browser');
    }

    const text = await navigator.clipboard.readText();
    return { value: text, type: 'text/plain' };
  }

  private async writeText(text: string) {
    if (!('writeText' in navigator.clipboard)) {
      throw new Error('Writting to clipboard not supported in this browser');
    }

    await navigator.clipboard.writeText(text);
  }

  private _getBlobData(clipboardBlob: Blob, type: string) {
    return new Promise<string>((resolve, reject) => {
      var reader = new FileReader();
      if (type.includes('image')) {
        reader.readAsDataURL(clipboardBlob);
      } else {
        reader.readAsText(clipboardBlob);
      }
      reader.onloadend = () => {
        const r = reader.result as string;
        resolve(r);
      };
      reader.onerror = e => {
        reject(e);
      };
    });
  }
}

const Clipboard = new ClipboardWeb();

export { Clipboard };
