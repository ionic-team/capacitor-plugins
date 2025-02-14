import type { GetResult, SetOptions, TextZoomPlugin } from './definitions';

export class TextZoomIOS implements TextZoomPlugin {
  static readonly TEXT_SIZE_REGEX = /(\d+)%/;

  async get(): Promise<GetResult> {
    const percentage = this.getRaw();
    const value = this.textSizePercentageToNumber(percentage);

    return { value };
  }

  async getPreferred(): Promise<never> {
    throw 'Native implementation will be used';
  }

  async set(options: SetOptions): Promise<void> {
    const num = this.textSizeNumberToPercentage(options.value);
    this.setRaw(num);
  }

  getRaw(): string {
    if (typeof document !== 'undefined') {
      return document.body.style.webkitTextSizeAdjust || '100%';
    }
    return '100%';
  }

  setRaw(value: string): void {
    if (typeof document !== 'undefined') {
      document.body.style.webkitTextSizeAdjust = value;
    }
  }

  textSizePercentageToNumber(percentage: string): number {
    const m = TextZoomIOS.TEXT_SIZE_REGEX.exec(percentage);

    if (!m) {
      return 1;
    }

    const parsed = Number.parseInt(m[1], 10);

    if (Number.isNaN(parsed)) {
      return 1;
    }

    return parsed / 100;
  }

  textSizeNumberToPercentage(num: number): string {
    return `${Math.round(num * 100)}%`;
  }
}
