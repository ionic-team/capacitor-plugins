import { WebPlugin } from '@capacitor/core';
import {
  StoragePlugin,
  GetOptions,
  GetResult,
  SetOptions,
  RemoveOptions,
  KeysResult,
} from './definitions';

export class StorageWeb extends WebPlugin implements StoragePlugin {
  KEY_PREFIX = '_cap_';

  constructor() {
    super({ name: 'Storage' });
  }

  public async get(options: GetOptions): Promise<GetResult> {
    const value = this.impl.getItem(this.applyPrefix(options.key));

    return { value };
  }

  public async set(options: SetOptions): Promise<void> {
    this.impl.setItem(this.applyPrefix(options.key), options.value);
  }

  public async remove(options: RemoveOptions): Promise<void> {
    this.impl.removeItem(this.applyPrefix(options.key));
  }

  public async keys(): Promise<KeysResult> {
    const keys = this.rawKeys().map(k => k.substr(this.KEY_PREFIX.length));

    return { keys };
  }

  public async clear(): Promise<void> {
    for (const key of this.rawKeys()) {
      this.impl.removeItem(key);
    }
  }

  private get impl(): Storage {
    return window.localStorage;
  }

  private rawKeys(): string[] {
    return Object.keys(this.impl).filter(k => k.indexOf(this.KEY_PREFIX) === 0);
  }

  private applyPrefix(key: string) {
    return this.KEY_PREFIX + key;
  }
}
