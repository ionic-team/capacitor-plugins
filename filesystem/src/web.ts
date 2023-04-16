import { WebPlugin } from '@capacitor/core';

import type {
  AppendFileOptions,
  CopyOptions,
  CopyResult,
  DeleteFileOptions,
  FilesystemPlugin,
  GetUriOptions,
  GetUriResult,
  MkdirOptions,
  PermissionStatus,
  ReadFileOptions,
  ReadFileResult,
  ReaddirOptions,
  ReaddirResult,
  RenameOptions,
  RmdirOptions,
  StatOptions,
  StatResult,
  WriteFileOptions,
  WriteFileResult,
  Directory,
} from './definitions';

function resolve(path: string): string {
  const posix = path.split('/').filter(item => item !== '.');
  const newPosix: string[] = [];

  posix.forEach(item => {
    if (
      item === '..' &&
      newPosix.length > 0 &&
      newPosix[newPosix.length - 1] !== '..'
    ) {
      newPosix.pop();
    } else {
      newPosix.push(item);
    }
  });

  return newPosix.join('/');
}
function isPathParent(parent: string, children: string): boolean {
  parent = resolve(parent);
  children = resolve(children);
  const pathsA = parent.split('/');
  const pathsB = children.split('/');

  return (
    parent !== children &&
    pathsA.every((value, index) => value === pathsB[index])
  );
}

function b64ToUint6(nChr: number) {
  return nChr > 64 && nChr < 91
    ? nChr - 65
    : nChr > 96 && nChr < 123
    ? nChr - 71
    : nChr > 47 && nChr < 58
    ? nChr + 4
    : nChr === 43
    ? 62
    : nChr === 47
    ? 63
    : 0;
}

function base64ToUint8(sBase64: string, nBlocksSize?: number) {
  const sB64Enc = sBase64.replace(/[^A-Za-z0-9+/]/g, ''); // Remove any non-base64 characters, such as trailing "=", whitespace, and more.
  const nInLen = sB64Enc.length;
  const nOutLen = nBlocksSize
    ? Math.ceil(((nInLen * 3 + 1) >> 2) / nBlocksSize) * nBlocksSize
    : (nInLen * 3 + 1) >> 2;
  const taBytes = new Uint8Array(nOutLen);

  let nMod3;
  let nMod4;
  let nUint24 = 0;
  let nOutIdx = 0;
  for (let nInIdx = 0; nInIdx < nInLen; nInIdx++) {
    nMod4 = nInIdx & 3;
    nUint24 |= b64ToUint6(sB64Enc.charCodeAt(nInIdx)) << (6 * (3 - nMod4));
    if (nMod4 === 3 || nInLen - nInIdx === 1) {
      nMod3 = 0;
      while (nMod3 < 3 && nOutIdx < nOutLen) {
        taBytes[nOutIdx] = (nUint24 >>> ((16 >>> nMod3) & 24)) & 255;
        nMod3++;
        nOutIdx++;
      }
      nUint24 = 0;
    }
  }

  return taBytes;
}

/* Base64 string to array encoding */
function uint6ToB64(nUint6: number) {
  return nUint6 < 26
    ? nUint6 + 65
    : nUint6 < 52
    ? nUint6 + 71
    : nUint6 < 62
    ? nUint6 - 4
    : nUint6 === 62
    ? 43
    : nUint6 === 63
    ? 47
    : 65;
}

function uint8ToBase64(aBytes: Uint8Array) {
  let nMod3 = 2;
  let sB64Enc = '';

  const nLen = aBytes.length;
  let nUint24 = 0;
  for (let nIdx = 0; nIdx < nLen; nIdx++) {
    nMod3 = nIdx % 3;
    // To break your base64 into several 80-character lines, add:
    //   if (nIdx > 0 && ((nIdx * 4) / 3) % 76 === 0) {
    //      sB64Enc += "\r\n";
    //    }

    nUint24 |= aBytes[nIdx] << ((16 >>> nMod3) & 24);
    if (nMod3 === 2 || aBytes.length - nIdx === 1) {
      sB64Enc += String.fromCodePoint(
        uint6ToB64((nUint24 >>> 18) & 63),
        uint6ToB64((nUint24 >>> 12) & 63),
        uint6ToB64((nUint24 >>> 6) & 63),
        uint6ToB64(nUint24 & 63),
      );
      nUint24 = 0;
    }
  }
  return (
    sB64Enc.substring(0, sB64Enc.length - 2 + nMod3) +
    (nMod3 === 2 ? '' : nMod3 === 1 ? '=' : '==')
  );
}

let decoderUtf8: TextDecoder | null = null;
// eslint-disable-next-line @typescript-eslint/adjacent-overload-signatures
function uint8ToUTF8(uint8: Uint8Array): string {
  decoderUtf8 ??= new TextDecoder('utf-8');
  return decoderUtf8.decode(uint8);
}
let encoderUtf8: TextEncoder | null = null;
function utf8ToUint8(utf8: string): Uint8Array {
  encoderUtf8 ??= new TextEncoder();
  return encoderUtf8.encode(utf8);
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
const fromBase64 =
  typeof atob == 'function'
    ? atob
    : (() => {
        const b64tab = (a => {
          const tab: Record<string, number> = {};
          a.forEach((c, i) => (tab[c] = i));
          return tab;
        })(b64ch);
        const b64re =
          /^(?:[A-Za-z\d+/]{4})*?(?:[A-Za-z\d+/]{2}(?:==)?|[A-Za-z\d+/]{3}=?)?$/;
        const _fromCC = String.fromCharCode.bind(String);

        return function (asc: string) {
          // console.log('polyfilled');
          asc = asc.replace(/\s+/g, '');
          if (!b64re.test(asc)) throw new TypeError('malformed base64.');
          asc += '=='.slice(2 - (asc.length & 3));
          let u24,
            bin = '',
            r1,
            r2;
          for (let i = 0; i < asc.length; ) {
            u24 =
              (b64tab[asc.charAt(i++)] << 18) |
              (b64tab[asc.charAt(i++)] << 12) |
              ((r1 = b64tab[asc.charAt(i++)]) << 6) |
              (r2 = b64tab[asc.charAt(i++)]);
            bin +=
              r1 === 64
                ? _fromCC((u24 >> 16) & 255)
                : r2 === 64
                ? _fromCC((u24 >> 16) & 255, (u24 >> 8) & 255)
                : _fromCC((u24 >> 16) & 255, (u24 >> 8) & 255, u24 & 255);
          }
          return bin;
        };
      })();

export class FilesystemWeb extends WebPlugin implements FilesystemPlugin {
  DB_VERSION = 1;
  DB_NAME = 'Disc';

  private _writeCmds: string[] = ['add', 'put', 'delete'];
  private _db?: IDBDatabase;
  static _debug = true;
  async initDb(): Promise<IDBDatabase> {
    if (this._db !== undefined) {
      return this._db;
    }
    if (!('indexedDB' in window)) {
      throw this.unavailable("This browser doesn't support IndexedDB");
    }

    return new Promise<IDBDatabase>((resolve, reject) => {
      const request = indexedDB.open(this.DB_NAME, this.DB_VERSION);
      request.onupgradeneeded = FilesystemWeb.doUpgrade;
      request.onsuccess = () => {
        this._db = request.result;
        resolve(request.result);
      };
      request.onerror = () => reject(request.error);
      request.onblocked = () => {
        console.warn('db blocked');
      };
    });
  }

  static doUpgrade(event: IDBVersionChangeEvent): void {
    const eventTarget = event.target as IDBOpenDBRequest;
    const db = eventTarget.result;
    switch (event.oldVersion) {
      case 0:
      case 1:
      default: {
        if (db.objectStoreNames.contains('FileStorage')) {
          db.deleteObjectStore('FileStorage');
        }
        const store = db.createObjectStore('FileStorage', { keyPath: 'path' });
        store.createIndex('by_folder', 'folder');
      }
    }
  }

  async dbRequest(cmd: string, args: any[]): Promise<any> {
    const readFlag =
      this._writeCmds.indexOf(cmd) !== -1 ? 'readwrite' : 'readonly';
    return this.initDb().then((conn: IDBDatabase) => {
      return new Promise<IDBObjectStore>((resolve, reject) => {
        const tx: IDBTransaction = conn.transaction(['FileStorage'], readFlag);
        const store: any = tx.objectStore('FileStorage');
        const req = store[cmd](...args);
        req.onsuccess = () => resolve(req.result);
        req.onerror = () => reject(req.error);
      });
    });
  }

  async dbIndexRequest(
    indexName: string,
    cmd: string,
    args: [any],
  ): Promise<any> {
    const readFlag =
      this._writeCmds.indexOf(cmd) !== -1 ? 'readwrite' : 'readonly';
    return this.initDb().then((conn: IDBDatabase) => {
      return new Promise<IDBObjectStore>((resolve, reject) => {
        const tx: IDBTransaction = conn.transaction(['FileStorage'], readFlag);
        const store: IDBObjectStore = tx.objectStore('FileStorage');
        const index: any = store.index(indexName);
        const req = index[cmd](...args) as any;
        req.onsuccess = () => resolve(req.result);
        req.onerror = () => reject(req.error);
      });
    });
  }

  private getPath(
    directory: Directory | undefined,
    uriPath: string | undefined,
  ): string {
    const cleanedUriPath =
      uriPath !== undefined ? uriPath.replace(/^[/]+|[/]+$/g, '') : '';
    let fsPath = '';
    if (directory !== undefined) fsPath += '/' + directory;
    if (uriPath !== '') fsPath += '/' + cleanedUriPath;
    return fsPath;
  }

  async clear(): Promise<void> {
    const conn: IDBDatabase = await this.initDb();
    const tx: IDBTransaction = conn.transaction(['FileStorage'], 'readwrite');
    const store: IDBObjectStore = tx.objectStore('FileStorage');
    store.clear();
  }

  /**
   * Read a file from disk
   * @param options options for the file read
   * @return a promise that resolves with the read file data result
   */
  async readFile(options: ReadFileOptions): Promise<ReadFileResult> {
    const path: string = this.getPath(options.directory, options.path);
    // const encoding = options.encoding;

    const entry = (await this.dbRequest('get', [path])) as EntryObj;
    if (entry === undefined) throw Error('File does not exist.');

    let { content: data = '' } = entry;

    if (options.encoding) {
      if (typeof data !== 'string') {
        // is uint8array
        data = uint8ToUTF8(data);
      }
    } else {
      if (typeof data === 'string') data = toBase64(data);
      else data = uint8ToBase64(data);
    }
    return { data };
  }

  /**
   * Write a file to disk in the specified location on device
   * @param options options for the file write
   * @return a promise that resolves with the file write result
   */
  async writeFile(options: WriteFileOptions): Promise<WriteFileResult> {
    const path: string = this.getPath(options.directory, options.path);
    let data: Uint8Array | string = options.data;
    const encoding = options.encoding;
    const doRecursive = options.recursive;

    const occupiedEntry = (await this.dbRequest('get', [path])) as EntryObj;
    if (occupiedEntry && occupiedEntry.type === 'directory')
      throw Error('The supplied path is a directory.');

    const parentPath = path.slice(0, path.lastIndexOf('/'));

    const parentEntry = (await this.dbRequest('get', [parentPath])) as EntryObj;
    if (parentEntry === undefined) {
      const subDirIndex = parentPath.indexOf('/', 1);
      if (subDirIndex !== -1) {
        const parentArgPath = parentPath.slice(subDirIndex);
        await this.mkdir({
          path: parentArgPath,
          directory: options.directory,
          recursive: doRecursive,
        });
      }
    }

    if (!encoding) {
      data = data.indexOf(',') >= 0 ? data.split(',')[1] : data;
      if (!this.isBase64String(data))
        throw Error('The supplied data is not valid base64 content.');

      data = base64ToUint8(data);
    }

    const now = Date.now();
    const pathObj: EntryObj = {
      path: path,
      folder: parentPath,
      type: 'file',
      size: data.length,
      ctime: now,
      mtime: now,
      content: data,
    };
    await this.dbRequest('put', [pathObj]);
    return {
      uri: pathObj.path,
    };
  }

  /**
   * Append to a file on disk in the specified location on device
   * @param options options for the file append
   * @return a promise that resolves with the file write result
   */
  async appendFile(options: AppendFileOptions): Promise<void> {
    const path: string = this.getPath(options.directory, options.path);
    let data: Uint8Array | string = options.data;
    const encoding = options.encoding;
    const parentPath = path.slice(0, path.lastIndexOf('/'));

    const now = Date.now();
    let ctime = now;

    const occupiedEntry = (await this.dbRequest('get', [path])) as EntryObj;
    if (occupiedEntry && occupiedEntry.type === 'directory')
      throw Error('The supplied path is a directory.');

    const parentEntry = (await this.dbRequest('get', [parentPath])) as EntryObj;
    if (parentEntry === undefined) {
      const subDirIndex = parentPath.indexOf('/', 1);
      if (subDirIndex !== -1) {
        const parentArgPath = parentPath.slice(subDirIndex);
        await this.mkdir({
          path: parentArgPath,
          directory: options.directory,
          recursive: true,
        });
      }
    }

    if (!encoding && !this.isBase64String(data))
      throw Error('The supplied data is not valid base64 content.');

    if (occupiedEntry) {
      if (!encoding) {
        // data is base64
        if (typeof occupiedEntry.content === 'undefined')
          data = base64ToUint8(data);
        else if (typeof occupiedEntry.content === 'string')
          data = occupiedEntry.content + fromBase64(data);
        else {
          const dataArr = base64ToUint8(data);
          const _temp = new Uint8Array(
            occupiedEntry.content.length + dataArr.length,
          );
          _temp.set(occupiedEntry.content, 0);
          _temp.set(_temp.slice(occupiedEntry.content.length), dataArr.length);
          data = _temp;
        }
      } else {
        // data is text
        if (typeof occupiedEntry.content === 'string')
          data = occupiedEntry.content + data;
        else if (typeof occupiedEntry.content !== 'undefined') {
          const dataArr = utf8ToUint8(data);
          const _temp = new Uint8Array(
            occupiedEntry.content.length + dataArr.length,
          );
          _temp.set(occupiedEntry.content, 0);
          _temp.set(_temp.slice(occupiedEntry.content.length), dataArr.length);
          data = _temp;
        }
      }

      ctime = occupiedEntry.ctime;
    }

    const pathObj: EntryObj = {
      path: path,
      folder: parentPath,
      type: 'file',
      size: data.length,
      ctime: ctime,
      mtime: now,
      content: data,
    };
    await this.dbRequest('put', [pathObj]);
  }

  /**
   * Delete a file from disk
   * @param options options for the file delete
   * @return a promise that resolves with the deleted file data result
   */
  async deleteFile(options: DeleteFileOptions): Promise<void> {
    const path: string = this.getPath(options.directory, options.path);

    const entry = (await this.dbRequest('get', [path])) as EntryObj;
    if (entry === undefined) throw Error('File does not exist.');
    const entries = await this.dbIndexRequest('by_folder', 'getAllKeys', [
      IDBKeyRange.only(path),
    ]);
    if (entries.length !== 0) throw Error('Folder is not empty.');

    await this.dbRequest('delete', [path]);
  }

  /**
   * Create a directory.
   * @param options options for the mkdir
   * @return a promise that resolves with the mkdir result
   */
  async mkdir(options: MkdirOptions): Promise<void> {
    const path: string = this.getPath(options.directory, options.path);
    const doRecursive = options.recursive;
    const parentPath = path.slice(0, path.lastIndexOf('/'));

    const depth = (path.match(/\//g) || []).length;
    const parentEntry = (await this.dbRequest('get', [parentPath])) as EntryObj;
    const occupiedEntry = (await this.dbRequest('get', [path])) as EntryObj;
    if (depth === 1) throw Error('Cannot create Root directory');
    if (occupiedEntry !== undefined)
      throw Error('Current directory does already exist.');
    if (!doRecursive && depth !== 2 && parentEntry === undefined)
      throw Error('Parent directory must exist');

    if (doRecursive && depth !== 2 && parentEntry === undefined) {
      const parentArgPath = parentPath.slice(parentPath.indexOf('/', 1));
      await this.mkdir({
        path: parentArgPath,
        directory: options.directory,
        recursive: doRecursive,
      });
    }
    const now = Date.now();
    const pathObj: EntryObj = {
      path: path,
      folder: parentPath,
      type: 'directory',
      size: 0,
      ctime: now,
      mtime: now,
    };
    await this.dbRequest('put', [pathObj]);
  }

  /**
   * Remove a directory
   * @param options the options for the directory remove
   */
  async rmdir(options: RmdirOptions): Promise<void> {
    const { path, directory, recursive } = options;
    const fullPath: string = this.getPath(directory, path);

    const entry = (await this.dbRequest('get', [fullPath])) as EntryObj;

    if (entry === undefined) throw Error('Folder does not exist.');

    if (entry.type !== 'directory')
      throw Error('Requested path is not a directory');

    const readDirResult = await this.readdir({ path, directory });

    if (readDirResult.files.length !== 0 && !recursive)
      throw Error('Folder is not empty');

    for (const entry of readDirResult.files) {
      const entryPath = `${path}/${entry.name}`;
      const entryObj = await this.stat({ path: entryPath, directory });
      if (entryObj.type === 'file') {
        await this.deleteFile({ path: entryPath, directory });
      } else {
        await this.rmdir({ path: entryPath, directory, recursive });
      }
    }

    await this.dbRequest('delete', [fullPath]);
  }

  /**
   * Return a list of files from the directory (not recursive)
   * @param options the options for the readdir operation
   * @return a promise that resolves with the readdir directory listing result
   */
  async readdir(options: ReaddirOptions): Promise<ReaddirResult> {
    const path: string = this.getPath(options.directory, options.path);

    const entry = (await this.dbRequest('get', [path])) as EntryObj;
    if (options.path !== '' && entry === undefined)
      throw Error('Folder does not exist.');

    const entries: string[] = await this.dbIndexRequest(
      'by_folder',
      'getAllKeys',
      [IDBKeyRange.only(path)],
    );
    const files = await Promise.all(
      entries.map(async e => {
        let subEntry = (await this.dbRequest('get', [e])) as EntryObj;
        if (subEntry === undefined) {
          subEntry = (await this.dbRequest('get', [e + '/'])) as EntryObj;
        }
        return {
          name: e.substring(path.length + 1),
          type: subEntry.type,
          size: subEntry.size,
          ctime: subEntry.ctime,
          mtime: subEntry.mtime,
          uri: subEntry.path,
        };
      }),
    );
    return { files: files };
  }

  /**
   * Return full File URI for a path and directory
   * @param options the options for the stat operation
   * @return a promise that resolves with the file stat result
   */
  async getUri(options: GetUriOptions): Promise<GetUriResult> {
    const path: string = this.getPath(options.directory, options.path);

    let entry = (await this.dbRequest('get', [path])) as EntryObj;
    if (entry === undefined) {
      entry = (await this.dbRequest('get', [path + '/'])) as EntryObj;
    }
    return {
      uri: entry?.path || path,
    };
  }

  /**
   * Return data about a file
   * @param options the options for the stat operation
   * @return a promise that resolves with the file stat result
   */
  async stat(options: StatOptions): Promise<StatResult> {
    const path: string = this.getPath(options.directory, options.path);

    let entry = (await this.dbRequest('get', [path])) as EntryObj;
    if (entry === undefined) {
      entry = (await this.dbRequest('get', [path + '/'])) as EntryObj;
    }
    if (entry === undefined) throw Error('Entry does not exist.');

    return {
      type: entry.type,
      size: entry.size,
      ctime: entry.ctime,
      mtime: entry.mtime,
      uri: entry.path,
    };
  }

  /**
   * Rename a file or directory
   * @param options the options for the rename operation
   * @return a promise that resolves with the rename result
   */
  async rename(options: RenameOptions): Promise<void> {
    await this._copy(options, true);
    return;
  }

  /**
   * Copy a file or directory
   * @param options the options for the copy operation
   * @return a promise that resolves with the copy result
   */
  async copy(options: CopyOptions): Promise<CopyResult> {
    return this._copy(options, false);
  }

  async requestPermissions(): Promise<PermissionStatus> {
    return { publicStorage: 'granted' };
  }

  async checkPermissions(): Promise<PermissionStatus> {
    return { publicStorage: 'granted' };
  }

  /**
   * Function that can perform a copy or a rename
   * @param options the options for the rename operation
   * @param doRename whether to perform a rename or copy operation
   * @return a promise that resolves with the result
   */
  private async _copy(
    options: CopyOptions,
    doRename = false,
  ): Promise<CopyResult> {
    let { toDirectory } = options;
    const { to, from, directory: fromDirectory } = options;

    if (!to || !from) {
      throw Error('Both to and from must be provided');
    }

    // If no "to" directory is provided, use the "from" directory
    if (!toDirectory) {
      toDirectory = fromDirectory;
    }

    const fromPath = this.getPath(fromDirectory, from);
    const toPath = this.getPath(toDirectory, to);

    // Test that the "to" and "from" locations are different
    if (fromPath === toPath) {
      return {
        uri: toPath,
      };
    }

    if (isPathParent(fromPath, toPath)) {
      throw Error('To path cannot contain the from path');
    }

    // Check the state of the "to" location
    let toObj;
    try {
      toObj = await this.stat({
        path: to,
        directory: toDirectory,
      });
    } catch (e) {
      // To location does not exist, ensure the directory containing "to" location exists and is a directory
      const toPathComponents = to.split('/');
      toPathComponents.pop();
      const toPath = toPathComponents.join('/');

      // Check the containing directory of the "to" location exists
      if (toPathComponents.length > 0) {
        const toParentDirectory = await this.stat({
          path: toPath,
          directory: toDirectory,
        });

        if (toParentDirectory.type !== 'directory') {
          throw new Error('Parent directory of the to path is a file');
        }
      }
    }

    // Cannot overwrite a directory
    if (toObj && toObj.type === 'directory') {
      throw new Error('Cannot overwrite a directory with a file');
    }

    // Ensure the "from" object exists
    const fromObj = await this.stat({
      path: from,
      directory: fromDirectory,
    });

    // Set the mtime/ctime of the supplied path
    const updateTime = async (path: string, ctime: number, mtime: number) => {
      const fullPath: string = this.getPath(toDirectory, path);
      const entry = (await this.dbRequest('get', [fullPath])) as EntryObj;
      entry.ctime = ctime;
      entry.mtime = mtime;
      await this.dbRequest('put', [entry]);
    };

    const ctime = fromObj.ctime ? fromObj.ctime : Date.now();

    switch (fromObj.type) {
      // The "from" object is a file
      case 'file': {
        // Read the file
        const file = await this.readFile({
          path: from,
          directory: fromDirectory,
        });

        // Optionally remove the file
        if (doRename) {
          await this.deleteFile({
            path: from,
            directory: fromDirectory,
          });
        }

        // Write the file to the new location
        const writeResult = await this.writeFile({
          path: to,
          directory: toDirectory,
          data: file.data, // is always base64 encoded
        });

        // Copy the mtime/ctime of a renamed file
        if (doRename) {
          await updateTime(to, ctime, fromObj.mtime);
        }

        // Resolve promise
        return writeResult;
      }
      case 'directory': {
        if (toObj) {
          throw Error('Cannot move a directory over an existing object');
        }

        try {
          // Create the to directory
          await this.mkdir({
            path: to,
            directory: toDirectory,
            recursive: false,
          });

          // Copy the mtime/ctime of a renamed directory
          if (doRename) {
            await updateTime(to, ctime, fromObj.mtime);
          }
        } catch (e) {
          // ignore
        }

        // Iterate over the contents of the from location
        const contents = (
          await this.readdir({
            path: from,
            directory: fromDirectory,
          })
        ).files;

        for (const filename of contents) {
          // Move item from the from directory to the to directory
          await this._copy(
            {
              from: `${from}/${filename.name}`,
              to: `${to}/${filename.name}`,
              directory: fromDirectory,
              toDirectory,
            },
            doRename,
          );
        }

        // Optionally remove the original from directory
        if (doRename) {
          await this.rmdir({
            path: from,
            directory: fromDirectory,
          });
        }
      }
    }
    return {
      uri: toPath,
    };
  }

  private isBase64String(str: string): boolean {
    try {
      return btoa(atob(str)) == str;
    } catch (err) {
      return false;
    }
  }
}

interface EntryObj {
  path: string;
  folder: string;
  type: 'directory' | 'file';
  size: number;
  ctime: number;
  mtime: number;
  uri?: string;
  content?: string | Uint8Array;
}
