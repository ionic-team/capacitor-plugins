import { WebPlugin } from '@capacitor/core';

import type {
  AppendFileOptions,
  CopyOptions,
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
    return { data: entry.content ? entry.content : '' };
  }

  /**
   * Write a file to disk in the specified location on device
   * @param options options for the file write
   * @return a promise that resolves with the file write result
   */
  async writeFile(options: WriteFileOptions): Promise<WriteFileResult> {
    const path: string = this.getPath(options.directory, options.path);
    const data = options.data;
    const doRecursive = options.recursive;

    const occupiedEntry = (await this.dbRequest('get', [path])) as EntryObj;
    if (occupiedEntry && occupiedEntry.type === 'directory')
      throw 'The supplied path is a directory.';

    const encoding = options.encoding;
    const parentPath = path.substr(0, path.lastIndexOf('/'));

    const parentEntry = (await this.dbRequest('get', [parentPath])) as EntryObj;
    if (parentEntry === undefined) {
      const subDirIndex = parentPath.indexOf('/', 1);
      if (subDirIndex !== -1) {
        const parentArgPath = parentPath.substr(subDirIndex);
        await this.mkdir({
          path: parentArgPath,
          directory: options.directory,
          recursive: doRecursive,
        });
      }
    }
    const now = Date.now();
    const pathObj: EntryObj = {
      path: path,
      folder: parentPath,
      type: 'file',
      size: data.length,
      ctime: now,
      mtime: now,
      content: !encoding && data.indexOf(',') >= 0 ? data.split(',')[1] : data,
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
    let data = options.data;
    // const encoding = options.encoding;
    const parentPath = path.substr(0, path.lastIndexOf('/'));

    const now = Date.now();
    let ctime = now;

    const occupiedEntry = (await this.dbRequest('get', [path])) as EntryObj;
    if (occupiedEntry && occupiedEntry.type === 'directory')
      throw 'The supplied path is a directory.';

    const parentEntry = (await this.dbRequest('get', [parentPath])) as EntryObj;
    if (parentEntry === undefined) {
      const subDirIndex = parentPath.indexOf('/', 1);
      if (subDirIndex !== -1) {
        const parentArgPath = parentPath.substr(subDirIndex);
        await this.mkdir({
          path: parentArgPath,
          directory: options.directory,
          recursive: true,
        });
      }
    }

    if (occupiedEntry !== undefined) {
      data = occupiedEntry.content + data;
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
    const parentPath = path.substr(0, path.lastIndexOf('/'));

    const depth = (path.match(/\//g) || []).length;
    const parentEntry = (await this.dbRequest('get', [parentPath])) as EntryObj;
    const occupiedEntry = (await this.dbRequest('get', [path])) as EntryObj;
    if (depth === 1) throw Error('Cannot create Root directory');
    if (occupiedEntry !== undefined)
      throw Error('Current directory does already exist.');
    if (!doRecursive && depth !== 2 && parentEntry === undefined)
      throw Error('Parent directory must exist');

    if (doRecursive && depth !== 2 && parentEntry === undefined) {
      const parentArgPath = parentPath.substr(parentPath.indexOf('/', 1));
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
      const entryPath = `${path}/${entry}`;
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
    const names = entries.map(e => {
      return e.substring(path.length + 1);
    });
    return { files: names };
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
    return this._copy(options, true);
  }

  /**
   * Copy a file or directory
   * @param options the options for the copy operation
   * @return a promise that resolves with the copy result
   */
  async copy(options: CopyOptions): Promise<void> {
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
  private async _copy(options: CopyOptions, doRename = false): Promise<void> {
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
      return;
    }

    if (toPath.startsWith(fromPath)) {
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
        await this.writeFile({
          path: to,
          directory: toDirectory,
          data: file.data,
        });

        // Copy the mtime/ctime of a renamed file
        if (doRename) {
          await updateTime(to, fromObj.ctime, fromObj.mtime);
        }

        // Resolve promise
        return;
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
            await updateTime(to, fromObj.ctime, fromObj.mtime);
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
              from: `${from}/${filename}`,
              to: `${to}/${filename}`,
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
  }
}

interface EntryObj {
  path: string;
  folder: string;
  type: string;
  size: number;
  ctime: number;
  mtime: number;
  uri?: string;
  content?: string;
}
