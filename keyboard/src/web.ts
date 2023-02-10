import { WebPlugin } from '@capacitor/core';

import type {
    KeyboardPlugin,
    KeyboardResizeOptions,
    KeyboardStyleOptions,
} from './definitions';

export class KeyboardWeb extends WebPlugin implements KeyboardPlugin {
    show(): Promise<void> {
        return Promise.resolve(undefined);
    }

    hide(): Promise<void> {
        return Promise.resolve();
    }

    setAccessoryBarVisible(_: { isVisible: boolean }): Promise<void> {
        return Promise.resolve();
    }

    setScroll(_: { isDisabled: boolean }): Promise<void> {
        return Promise.resolve();
    }

    setStyle(_: KeyboardStyleOptions): Promise<void> {
        return Promise.resolve();
    }

    setResizeMode(_: KeyboardResizeOptions): Promise<void> {
        return Promise.resolve();
    }
}
