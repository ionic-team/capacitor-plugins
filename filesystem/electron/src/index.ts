import { app } from 'electron';
import {
  readFile,
  writeFile,
  appendFile,
  unlink,
  mkdir,
  rmdir,
  readdir,
  stat,
  rename,
  cp,
} from 'fs/promises';
import { tmpdir } from 'os';
import { join, normalize, sep, dirname, isAbsolute } from 'path';

import { Encoding, Directory } from '../../src/definitions';
import type {
  ReadFileOptions,
  ReadFileResult,
  WriteFileOptions,
  WriteFileResult,
  AppendFileOptions,
  DeleteFileOptions,
  MkdirOptions,
  RmdirOptions,
  ReaddirOptions,
  ReaddirResult,
  GetUriOptions,
  GetUriResult,
  StatOptions,
  StatResult,
  CopyOptions,
  PermissionStatus,
  FileInfo,
} from '../../src/definitions';

import type { FilesystemPlugin } from './definitions';

const encodingToNative = (encoding: Encoding | undefined): BufferEncoding => {
  switch (encoding) {
    case Encoding.UTF8:
      return 'utf8';
    case Encoding.ASCII:
      return 'ascii';
    case Encoding.UTF16:
      return 'utf16le';
    default:
      return 'base64';
  }
};

const directoryToNative = (directory: Directory): string => {
  const appName = app.getName();

  switch (directory) {
    case Directory.Data:
    case Directory.Library:
    case Directory.External:
      return app.isPackaged ? app.getAppPath() : app.getPath('userData');
    case Directory.Cache:
      return join(tmpdir(), appName);
    case Directory.ExternalStorage:
    case Directory.Documents:
    default:
      return app.getPath('userData');
  }
};

export class Filesystem implements FilesystemPlugin {
  readFile(options: ReadFileOptions): Promise<ReadFileResult> {
    const { path, directory, encoding } = options ?? {};

    Filesystem.checkPath(path);

    return readFile(Filesystem.getPath(directory, path), {
      encoding: encodingToNative(encoding),
    }).then(data => ({ data }));
  }

  async writeFile(options: WriteFileOptions): Promise<WriteFileResult> {
    const { path, directory, data, encoding, recursive = false } = options;

    Filesystem.checkPath(path);
    Filesystem.checkData(data);

    const fullPath = Filesystem.getPath(directory, path);

    if (recursive) {
      try {
        await mkdir(dirname(fullPath), { recursive: true });
      } catch (err) {
        if (err.code !== 'EEXIST') {
          throw err;
        }
      }
    }

    return writeFile(fullPath, data, {
      encoding: encodingToNative(encoding),
    }).then(() => ({ uri: this._getUri(fullPath) }));
  }

  async appendFile(options: AppendFileOptions): Promise<void> {
    const { path, directory, data, encoding } = options ?? {};

    Filesystem.checkPath(path);
    Filesystem.checkData(data);

    return appendFile(Filesystem.getPath(directory, path), data, {
      encoding: encodingToNative(encoding),
    });
  }

  deleteFile(options: DeleteFileOptions): Promise<void> {
    const { path, directory } = options ?? {};

    Filesystem.checkPath(path);

    return unlink(Filesystem.getPath(directory, path));
  }

  mkdir(options: MkdirOptions): Promise<void> {
    const { path, directory, recursive = false } = options ?? {};

    Filesystem.checkPath(path);

    return mkdir(Filesystem.getPath(directory, path), { recursive }).then(
      () => undefined,
    );
  }

  rmdir(options: RmdirOptions): Promise<void> {
    const { path, directory, recursive = false } = options ?? {};

    Filesystem.checkPath(path);

    return rmdir(Filesystem.getPath(directory, path), { recursive });
  }

  async readdir(options: ReaddirOptions): Promise<ReaddirResult> {
    const { path, directory } = options ?? {};

    const resolvedPath = Filesystem.getPath(directory, path ?? '');
    const entries = await readdir(resolvedPath);

    const fileInfo = await Promise.all(
      entries.map<Promise<FileInfo>>(async entry => {
        const entryPath = join(resolvedPath, entry);
        const stats = await stat(entryPath);

        return {
          type: stats.isDirectory() ? 'directory' : 'file',
          uri: this._getUri(entryPath),
          ctime: stats.ctimeMs,
          mtime: stats.mtimeMs,
          size: stats.size,
          name: entry,
        };
      }),
    );

    return { files: fileInfo };
  }

  getUri(options: GetUriOptions): Promise<GetUriResult> {
    const { path, directory } = options ?? {};

    Filesystem.checkPath(path);

    return Promise.resolve({
      uri: this._getUri(Filesystem.getPath(directory, path)),
    });
  }

  stat(options: StatOptions): Promise<StatResult> {
    const { path, directory } = options ?? {};

    Filesystem.checkPath(path);

    const fullPath = Filesystem.getPath(directory, path);

    return stat(fullPath).then(value => ({
      type: value.isDirectory() ? 'directory' : 'file',
      ctime: value.ctime.getTime(),
      mtime: value.mtime.getTime(),
      uri: this._getUri(fullPath),
      size: value.size,
    }));
  }

  rename(options: CopyOptions): Promise<void> {
    const { from, to, directory, toDirectory } = options ?? {};

    Filesystem.checkPath(from);
    Filesystem.checkPath(to);

    return rename(
      Filesystem.getPath(directory, from),
      Filesystem.getPath(toDirectory ?? directory, to),
    );
  }

  copy(options: CopyOptions): Promise<void> {
    const { from, to, directory, toDirectory } = options ?? {};

    Filesystem.checkPath(from);
    Filesystem.checkPath(to);

    return cp(
      Filesystem.getPath(directory, from),
      Filesystem.getPath(toDirectory ?? directory, to),
    );
  }

  checkPermissions(): Promise<PermissionStatus> {
    return Promise.resolve({ publicStorage: 'granted' });
  }

  requestPermissions(): Promise<PermissionStatus> {
    return Promise.resolve({ publicStorage: 'granted' });
  }

  private _getUri(path: string): string {
    return new URL(`file://${path}`).toString();
  }

  private static checkPath(path: string) {
    if (!path) {
      throw new Error('Path not defined');
    }
  }

  private static checkData(data: string) {
    if (!data) {
      throw new Error('No data found');
    }
  }

  private static getPath(directory: Directory, path: string): string {
    // If the path is a URI, ignore the specified directory and allow access to the whole system
    try {
      const url = new URL(path);

      if (url.protocol === 'file:') {
        const pathName = normalize(
          process.platform === 'win32'
            ? url.pathname.substring(1)
            : url.pathname,
        );

        if (!isAbsolute(pathName)) {
          throw new Error('Protocol paths cannot be relative');
        }

        return pathName;
      }
      // eslint-disable-next-line no-empty
    } catch {}

    return join(
      directoryToNative(directory),
      normalize(path).replaceAll(`..${sep}`, ''),
    );
  }
}
