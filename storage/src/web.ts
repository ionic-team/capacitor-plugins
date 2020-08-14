import { WebPlugin } from '@capacitor/core';
import {
  StoragePlugin,
  ConfigureOptions,
  GetOptions,
  GetResult,
  SetOptions,
  RemoveOptions,
  KeysResult,
} from './definitions';

export class StorageWeb extends WebPlugin implements StoragePlugin {
  private group = 'CapacitorStorage';

  constructor() {
    super({ name: 'Storage' });
  }

  public async configure({ group }: ConfigureOptions): Promise<void> {
    if (typeof group === 'string') {
      this.group = group;
    }
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
    const keys = this.rawKeys().map(k => k.substring(this.prefix.length));

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

  private get prefix(): string {
    return this.group ? `${this.group}.` : '';
  }

  private rawKeys(): string[] {
    return Object.keys(this.impl).filter(k => k.indexOf(this.prefix) === 0);
  }

  private applyPrefix(key: string) {
    return this.prefix + key;
  }
}
