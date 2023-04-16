import 'fake-indexeddb/auto';
import { describe, beforeEach, expect, test } from 'vitest';

import { Directory, Encoding } from './definitions';
import { FilesystemWeb } from './web';

const Filesystem = new FilesystemWeb();

async function cleanupFS() {
  const { files } = await Filesystem.readdir({
    path: '',
    directory: Directory.External,
  }).catch(() => ({ files: [] }));

  for (const file of files) {
    if (file.type === 'file')
      await Filesystem.deleteFile({
        path: file.name,
        directory: Directory.External,
      });
    else
      await Filesystem.rmdir({
        path: file.name,
        directory: Directory.External,
        recursive: true,
      }).catch(() => false);
  }
}

const b64ch =
  'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/='.split('');
const toBase64 =
  typeof btoa == 'function'
    ? btoa
    : (() => {
        return function (bin: string) {
          let u32,
            c0,
            c1,
            c2,
            asc = '';
          const pad = bin.length % 3;
          for (let i = 0; i < bin.length; ) {
            if (
              (c0 = bin.charCodeAt(i++)) > 255 ||
              (c1 = bin.charCodeAt(i++)) > 255 ||
              (c2 = bin.charCodeAt(i++)) > 255
            )
              throw new TypeError('invalid character found');
            u32 = (c0 << 16) | (c1 << 8) | c2;
            asc +=
              b64ch[(u32 >> 18) & 63] +
              b64ch[(u32 >> 12) & 63] +
              b64ch[(u32 >> 6) & 63] +
              b64ch[u32 & 63];
          }
          return pad ? asc.slice(0, pad - 3) + '==='.substring(pad) : asc;
        };
      })();

describe('web', () => {
  beforeEach(cleanupFS);

  describe('writeFile', () => {
    test('should write utf8', async () => {
      await Filesystem.writeFile({
        path: 'test.bin',
        directory: Directory.External,
        data: 'hello world',
        encoding: Encoding.UTF8,
      });

      expect(
        await Filesystem.readFile({
          path: 'test.bin',
          directory: Directory.External,
        }),
      ).toEqual({
        data: toBase64('hello world'),
      });
      expect(
        await Filesystem.readFile({
          path: 'test.bin',
          directory: Directory.External,
          encoding: Encoding.UTF8,
        }),
      ).toEqual({ data: 'hello world' });
    });

    test('should write base64', async () => {
      await Filesystem.writeFile({
        path: 'test.bin',
        directory: Directory.External,
        data: toBase64('hello world'),
      });

      expect(
        await Filesystem.readFile({
          path: 'test.bin',
          directory: Directory.External,
        }),
      ).toEqual({
        data: toBase64('hello world'),
      });
      expect(
        await Filesystem.readFile({
          path: 'test.bin',
          directory: Directory.External,
          encoding: Encoding.UTF8,
        }),
      ).toEqual({ data: 'hello world' });
    });

    test('should write text base64', async () => {
      await Filesystem.writeFile({
        path: 'test.bin',
        directory: Directory.External,
        data: 'SGVsbG8sIHdvcmxkIQ==',
        encoding: Encoding.UTF8,
      });

      expect(
        await Filesystem.readFile({
          path: 'test.bin',
          directory: Directory.External,
        }),
      ).toEqual({
        data: toBase64('SGVsbG8sIHdvcmxkIQ=='),
      });
      expect(
        await Filesystem.readFile({
          path: 'test.bin',
          directory: Directory.External,
          encoding: Encoding.UTF8,
        }),
      ).toEqual({
        data: 'SGVsbG8sIHdvcmxkIQ==',
      });
    });

    test('should write directory', async () => {
      await Filesystem.mkdir({
        path: 'test',
        directory: Directory.External,
      });

      expect(
        Filesystem.writeFile({
          path: 'test',
          directory: Directory.External,
          encoding: Encoding.UTF8,
          data: 'hello',
        }),
      ).rejects.toThrowError('The supplied path is a directory.');
    });

    test('should write file to directory not exists', async () => {
      await Filesystem.writeFile({
        path: 'foo/test',
        directory: Directory.External,
        encoding: Encoding.UTF8,
        data: 'hello',
      });

      expect(
        await Filesystem.readFile({
          path: 'foo/test',
          directory: Directory.External,
          encoding: Encoding.UTF8,
        }),
      ).toEqual({
        data: 'hello',
      });
    });

    test('should write file to directory exists', async () => {
      await Filesystem.mkdir({
        path: 'foo2',
        directory: Directory.External,
        recursive: true,
      });

      await Filesystem.writeFile({
        path: 'foo2/test',
        directory: Directory.External,
        encoding: Encoding.UTF8,
        data: 'hello',
      });

      expect(
        await Filesystem.readFile({
          path: 'foo2/test',
          directory: Directory.External,
          encoding: Encoding.UTF8,
        }),
      ).toEqual({
        data: 'hello',
      });
    });

    test('should write base64url', async () => {
      await Filesystem.writeFile({
        path: 'test.bin',
        directory: Directory.External,
        data: 'data:base64,' + toBase64('hello world'),
      });

      expect(
        await Filesystem.readFile({
          path: 'test.bin',
          directory: Directory.External,
        }),
      ).toEqual({
        data: toBase64('hello world'),
      });
    });

    test('should write base64 but data is not base64', async () => {
      expect(
        Filesystem.writeFile({
          path: 'test',
          directory: Directory.External,
          data: 'hello',
        }),
      ).rejects.toThrowError('The supplied data is not valid base64 content.');
    });
  });

  describe('readFile', () => {
    test('should read utf8 file utf8', async () => {
      await Filesystem.writeFile({
        path: 'test.txt',
        directory: Directory.External,
        encoding: Encoding.UTF8,
        data: 'hello world',
      });

      expect(
        await Filesystem.readFile({
          path: 'test.txt',
          directory: Directory.External,
          encoding: Encoding.UTF8,
        }),
      ).toEqual({ data: 'hello world' });
    });

    test('should read base64 file utf8', async () => {
      await Filesystem.writeFile({
        path: 'test.txt',
        directory: Directory.External,
        encoding: Encoding.UTF8,
        data: 'hello world',
      });

      expect(
        await Filesystem.readFile({
          path: 'test.txt',
          directory: Directory.External,
        }),
      ).toEqual({ data: toBase64('hello world') });
    });

    test('should read utf8 file binary', async () => {
      await Filesystem.writeFile({
        path: 'test.bin',
        directory: Directory.External,
        data: toBase64('hello world'),
      });

      expect(
        await Filesystem.readFile({
          path: 'test.bin',
          directory: Directory.External,
          encoding: Encoding.UTF8,
        }),
      ).toEqual({ data: 'hello world' });
    });

    test('should read base64 file binary', async () => {
      await Filesystem.writeFile({
        path: 'test.bin',
        directory: Directory.External,
        data: toBase64('hello world'),
      });

      expect(
        await Filesystem.readFile({
          path: 'test.bin',
          directory: Directory.External,
        }),
      ).toEqual({ data: toBase64('hello world') });
    });

    test('should read file not exists', async () => {
      expect(
        Filesystem.readFile({
          path: 'test',
          directory: Directory.External,
        }),
      ).rejects.toThrowError('File does not exist.');
    });

    test('should read directory', async () => {
      await Filesystem.mkdir({
        path: 'test',
        directory: Directory.External,
      });

      expect(
        Filesystem.readFile({
          path: 'test',
          directory: Directory.External,
        }),
      ).rejects.toThrowError('Illegal operation on a directory.');
    });
  });

  describe('appendFile', () => {
    test('should append utf8 to file utf8', async () => {
      await Filesystem.writeFile({
        path: 'test.txt',
        directory: Directory.External,
        data: 'hello',
        encoding: Encoding.UTF8,
      });

      await Filesystem.appendFile({
        path: 'test.txt',
        directory: Directory.External,
        encoding: Encoding.UTF8,
        data: ' world',
      });

      expect(
        await Filesystem.readFile({
          path: 'test.txt',
          directory: Directory.External,
          encoding: Encoding.UTF8,
        }),
      ).toEqual({ data: 'hello world' });
    });

    test('should append utf8 file binary', async () => {
      await Filesystem.writeFile({
        path: 'test.txt',
        directory: Directory.External,
        data: toBase64('hello'),
      });

      await Filesystem.appendFile({
        path: 'test.txt',
        directory: Directory.External,
        encoding: Encoding.UTF8,
        data: ' world',
      });

      expect(
        await Filesystem.readFile({
          path: 'test.txt',
          directory: Directory.External,
          encoding: Encoding.UTF8,
        }),
      ).toEqual({ data: 'hello world' });
    });

    test('should append base64 to file binary', async () => {
      await Filesystem.writeFile({
        path: 'test.txt',
        directory: Directory.External,
        data: toBase64('hello'),
      });

      await Filesystem.appendFile({
        path: 'test.txt',
        directory: Directory.External,
        data: toBase64(' world'),
      });

      expect(
        await Filesystem.readFile({
          path: 'test.txt',
          directory: Directory.External,
          encoding: Encoding.UTF8,
        }),
      ).toEqual({ data: 'hello world' });
    });

    test('should append base64 file utf8', async () => {
      await Filesystem.writeFile({
        path: 'test.txt',
        directory: Directory.External,
        data: 'hello',
        encoding: Encoding.UTF8,
      });

      await Filesystem.appendFile({
        path: 'test.txt',
        directory: Directory.External,
        data: toBase64(' world'),
      });

      expect(
        await Filesystem.readFile({
          path: 'test.txt',
          directory: Directory.External,
          encoding: Encoding.UTF8,
        }),
      ).toEqual({ data: 'hello world' });
    });

    test('should append directory', async () => {
      await Filesystem.mkdir({
        path: 'test',
        directory: Directory.External,
      });

      expect(
        Filesystem.appendFile({
          path: 'test',
          directory: Directory.External,
          encoding: Encoding.UTF8,
          data: 'hello',
        }),
      ).rejects.toThrowError('The supplied path is a directory.');
    });

    test('should append file to directory not exists', async () => {
      await Filesystem.appendFile({
        path: 'foo/test',
        directory: Directory.External,
        encoding: Encoding.UTF8,
        data: 'hello',
      });

      expect(
        await Filesystem.readFile({
          path: 'foo/test',
          directory: Directory.External,
          encoding: Encoding.UTF8,
        }),
      ).toEqual({
        data: 'hello',
      });
    });

    test('should append file to directory exists', async () => {
      await Filesystem.mkdir({
        path: 'foo2',
        directory: Directory.External,
        recursive: true,
      });

      await Filesystem.appendFile({
        path: 'foo2/test',
        directory: Directory.External,
        encoding: Encoding.UTF8,
        data: 'hello',
      });

      expect(
        await Filesystem.readFile({
          path: 'foo2/test',
          directory: Directory.External,
          encoding: Encoding.UTF8,
        }),
      ).toEqual({
        data: 'hello',
      });
    });
  });

  describe('copy', () => {
    test('shoud copy file binary', async () => {
      await Filesystem.writeFile({
        path: 'test.bin',
        directory: Directory.External,
        data: toBase64('hello world'),
      });

      await Filesystem.copy({
        from: 'test.bin',
        to: 'hello.bin',
        directory: Directory.External,
        toDirectory: Directory.External,
      });

      expect(
        await Filesystem.readFile({
          path: 'hello.bin',
          directory: Directory.External,
          encoding: Encoding.UTF8,
        }),
      ).toEqual({ data: 'hello world' });
    });

    test('should copy file utf8', async () => {
      await Filesystem.writeFile({
        path: 'test.bin',
        directory: Directory.External,
        encoding: Encoding.UTF8,
        data: 'hello world',
      });

      await Filesystem.copy({
        from: 'test.bin',
        to: 'hello.bin',
        directory: Directory.External,
        toDirectory: Directory.External,
      });

      expect(
        await Filesystem.readFile({
          path: 'hello.bin',
          directory: Directory.External,
          encoding: Encoding.UTF8,
        }),
      ).toEqual({ data: 'hello world' });
    });

    test('should copy file utf8 content base64', async () => {
      await Filesystem.writeFile({
        path: 'test.bin',
        directory: Directory.External,
        encoding: Encoding.UTF8,
        data: toBase64('hello world'),
      });

      await Filesystem.copy({
        from: 'test.bin',
        to: 'hello.bin',
        directory: Directory.External,
        toDirectory: Directory.External,
      });

      expect(
        await Filesystem.readFile({
          path: 'hello.bin',
          directory: Directory.External,
          encoding: Encoding.UTF8,
        }),
      ).toEqual({ data: toBase64('hello world') });
    });

    test('shoud copy parent to children', async () => {
      expect(
        Filesystem.copy({
          from: 'parent',
          to: 'parent/child',
          directory: Directory.External,
          toDirectory: Directory.External,
        }),
      ).rejects.toThrowError('To path cannot contain the from path');
    });

    test('should copy to parent is directory', async () => {
      await Filesystem.writeFile({
        path: 'foo/test',
        directory: Directory.External,
        encoding: Encoding.UTF8,
        data: 'hello world',
      });

      await Filesystem.writeFile({
        path: 'test',
        directory: Directory.External,
        encoding: Encoding.UTF8,
        data: 'hello world',
      });

      expect(
        Filesystem.copy({
          from: 'test',
          to: 'foo/test/ge',
          directory: Directory.External,
        }),
      ).rejects.toThrowError('Parent directory of the to path is a file');
    });

    test('should copy directory to file', async () => {
      await Filesystem.writeFile({
        path: 'foo/test',
        directory: Directory.External,
        encoding: Encoding.UTF8,
        data: 'hello world',
      });

      await Filesystem.mkdir({
        path: 'test',
        directory: Directory.External,
      });

      expect(
        Filesystem.copy({
          from: 'test',
          to: 'foo/test',
          directory: Directory.External,
        }),
      ).rejects.toThrowError('Cannot move a directory over an existing object');
    });

    test('should copy file to directory', async () => {
      await Filesystem.writeFile({
        path: 'foo/test',
        directory: Directory.External,
        encoding: Encoding.UTF8,
        data: 'hello world',
      });

      await Filesystem.mkdir({
        path: 'test',
        directory: Directory.External,
      });

      expect(
        Filesystem.copy({
          to: 'test',
          from: 'foo/test',
          directory: Directory.External,
        }),
      ).rejects.toThrowError('Cannot overwrite a directory with a file');
    });
  });
});
