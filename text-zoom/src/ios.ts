import { TextZoomPlugin } from './definitions';

export class TextZoomIOS implements TextZoomPlugin {
  static readonly TEXT_SIZE_REGEX = /(\d+)%/;

  async get(): Promise<{ value: number }> {
    const percentage = this.getRaw();
    const value = this.textSizePercentageToNumber(percentage);
    console.log('percentage', percentage);
    console.log('value', value);

    return { value };
  }

  async getPreferred(): Promise<{ value: number }> {
    return { value: 1 };
  }

  async set(options: { value: number }): Promise<void> {
    const num = this.textSizeNumberToPercentage(options.value);
    console.log('num', num);
    this.setRaw(num);
  }

  getRaw(): string {
    return document.body.style.webkitTextSizeAdjust || '100%';
  }

  setRaw(value: string): void {
    document.body.style.webkitTextSizeAdjust = value;
  }

  textSizePercentageToNumber(percentage: string): number {
    const m = TextZoomIOS.TEXT_SIZE_REGEX.exec(percentage);

    console.log('re', m);

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
