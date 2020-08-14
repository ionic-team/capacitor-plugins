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
  private prefix = '_cap_';

  constructor() {
    super({ name: 'Storage' });
  }

  public async configure({ prefix }: ConfigureOptions): Promise<void> {
    if (typeof prefix === 'string') {
      if (prefix.length <= 1) {
        console.warn(
          `Storage: Extremely short prefixes (such as '${prefix}') may result in data loss.`,
        );
      }

      this.prefix = prefix;
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
    const keys = this.rawKeys().map(k => k.substr(this.prefix.length));

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
    return Object.keys(this.impl).filter(k => k.indexOf(this.prefix) === 0);
  }

  private applyPrefix(key: string) {
    return this.prefix + key;
  }
}
