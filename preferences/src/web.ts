import { WebPlugin } from '@capacitor/core';

import type {
  PreferencesPlugin,
  ConfigureOptions,
  GetOptions,
  GetResult,
  SetOptions,
  RemoveOptions,
  KeysResult,
  MigrateResult,
} from './definitions';

export class PreferencesWeb extends WebPlugin implements PreferencesPlugin {
  private group = 'CapacitorStorage';

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
    const keys = this.rawKeys().map((k) => k.substring(this.prefix.length));

    return { keys };
  }

  public async clear(): Promise<void> {
    for (const key of this.rawKeys()) {
      this.impl.removeItem(key);
    }
  }

  public async migrate(): Promise<MigrateResult> {
    const migrated: string[] = [];
    const existing: string[] = [];
    const oldprefix = '_cap_';
    const keys = Object.keys(this.impl).filter((k) => k.indexOf(oldprefix) === 0);

    for (const oldkey of keys) {
      const key = oldkey.substring(oldprefix.length);
      const value = this.impl.getItem(oldkey) ?? '';
      const { value: currentValue } = await this.get({ key });

      if (typeof currentValue === 'string') {
        existing.push(key);
      } else {
        await this.set({ key, value });
        migrated.push(key);
      }
    }

    return { migrated, existing };
  }

  public async removeOld(): Promise<void> {
    const oldprefix = '_cap_';
    const keys = Object.keys(this.impl).filter((k) => k.indexOf(oldprefix) === 0);
    for (const oldkey of keys) {
      this.impl.removeItem(oldkey);
    }
  }

  private get impl(): Storage {
    return window.localStorage;
  }

  private get prefix(): string {
    return this.group === 'NativeStorage' ? '' : `${this.group}.`;
  }

  private rawKeys(): string[] {
    return Object.keys(this.impl).filter((k) => k.indexOf(this.prefix) === 0);
  }

  private applyPrefix(key: string) {
    return this.prefix + key;
  }
}
