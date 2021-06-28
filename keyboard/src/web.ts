import { WebPlugin } from '@capacitor/core';

import type { KeyboardPlugin, KeyboardResizeOptions, KeyboardStyleOptions } from './definitions';

export class KeyboardWeb extends WebPlugin implements KeyboardPlugin {
    show(): Promise<void> {
        return Promise.resolve(undefined);
    }

    hide(): Promise<void> {
        return Promise.resolve();
    }

    setAccessoryBarVisible(options: { isVisible: boolean }): Promise<void> {
        return Promise.resolve();
    }

    setScroll(options: { isDisabled: boolean }): Promise<void> {
        return Promise.resolve();
    }

    setStyle(options: KeyboardStyleOptions): Promise<void> {
        return Promise.resolve();
    }

    setResizeMode(options: KeyboardResizeOptions): Promise<void> {
        return Promise.resolve();
    }
}
