import type {
  HttpOptions,
  PermissionState,
  PluginListenerHandle,
} from '@capacitor/core';

export interface PermissionStatus {
  publicStorage: PermissionState;
}

export enum Directory {
  /**
   * The Documents directory.
   * On iOS it's the app's documents directory.
   * Use this directory to store user-generated content.
   * On Android it's the Public Documents folder, so it's accessible from other apps.
   * It's not accesible on Android 10 unless the app enables legacy External Storage
   * by adding `android:requestLegacyExternalStorage="true"` in the `application` tag
   * in the `AndroidManifest.xml`.
   * On Android 11 or newer the app can only access the files/folders the app created.
   *
   * @since 1.0.0
   */
  Documents = 'DOCUMENTS',

  /**
   * The Data directory.
   * On iOS it will use the Documents directory.
   * On Android it's the directory holding application files.
   * Files will be deleted when the application is uninstalled.
   *
   * @since 1.0.0
   */
  Data = 'DATA',

  /**
   * The Library directory.
   * On iOS it will use the Library directory.
   * On Android it's the directory holding application files.
   * Files will be deleted when the application is uninstalled.
   *
   * @since 1.1.0
   */
  Library = 'LIBRARY',

  /**
   * The Cache directory.
   * Can be deleted in cases of low memory, so use this directory to write app-specific files.
   * that your app can re-create easily.
   *
   * @since 1.0.0
   */
  Cache = 'CACHE',

  /**
   * The external directory.
   * On iOS it will use the Documents directory.
   * On Android it's the directory on the primary shared/external
   * storage device where the application can place persistent files it owns.
   * These files are internal to the applications, and not typically visible
   * to the user as media.
   * Files will be deleted when the application is uninstalled.
   *
   * @since 1.0.0
   */
  External = 'EXTERNAL',

  /**
   * The external storage directory.
   * On iOS it will use the Documents directory.
   * On Android it's the primary shared/external storage directory.
   * It's not accesible on Android 10 unless the app enables legacy External Storage
   * by adding `android:requestLegacyExternalStorage="true"` in the `application` tag
   * in the `AndroidManifest.xml`.
   * It's not accesible on Android 11 or newer.
   *
   * @since 1.0.0
   */
  ExternalStorage = 'EXTERNAL_STORAGE',
}

export enum Encoding {
  /**
   * Eight-bit UCS Transformation Format
   *
   * @since 1.0.0
   */
  UTF8 = 'utf8',

  /**
   * Seven-bit ASCII, a.k.a. ISO646-US, a.k.a. the Basic Latin block of the
   * Unicode character set
   * This encoding is only supported on Android.
   *
   * @since 1.0.0
   */
  ASCII = 'ascii',

  /**
   * Sixteen-bit UCS Transformation Format, byte order identified by an
   * optional byte-order mark
   * This encoding is only supported on Android.
   *
   * @since 1.0.0
   */
  UTF16 = 'utf16',
}

export interface WriteFileOptions {
  /**
   * The path of the file to write
   *
   * @since 1.0.0
   */
  path: string;

  /**
   * The data to write
   *
   * Note: Blob data is only supported on Web.
   *
   * @since 1.0.0
   */
  data: string | Blob;

  /**
   * The `Directory` to store the file in
   *
   * @since 1.0.0
   */
  directory?: Directory;

  /**
   * The encoding to write the file in. If not provided, data
   * is written as base64 encoded.
   *
   * Pass Encoding.UTF8 to write data as string
   *
   * @since 1.0.0
   */
  encoding?: Encoding;

  /**
   * Whether to create any missing parent directories.
   *
   * @default false
   * @since 1.0.0
   */
  recursive?: boolean;
}

export interface AppendFileOptions {
  /**
   * The path of the file to append
   *
   * @since 1.0.0
   */
  path: string;

  /**
   * The data to write
   *
   * @since 1.0.0
   */
  data: string;

  /**
   * The `Directory` to store the file in
   *
   * @since 1.0.0
   */
  directory?: Directory;

  /**
   * The encoding to write the file in. If not provided, data
   * is written as base64 encoded.
   *
   * Pass Encoding.UTF8 to write data as string
   *
   * @since 1.0.0
   */
  encoding?: Encoding;
}

export interface ReadFileOptions {
  /**
   * The path of the file to read
   *
   * @since 1.0.0
   */
  path: string;

  /**
   * The `Directory` to read the file from
   *
   * @since 1.0.0
   */
  directory?: Directory;

  /**
   * The encoding to read the file in, if not provided, data
   * is read as binary and returned as base64 encoded.
   *
   * Pass Encoding.UTF8 to read data as string
   *
   * @since 1.0.0
   */
  encoding?: Encoding;
}

export interface DeleteFileOptions {
  /**
   * The path of the file to delete
   *
   * @since 1.0.0
   */
  path: string;

  /**
   * The `Directory` to delete the file from
   *
   * @since 1.0.0
   */
  directory?: Directory;
}

export interface MkdirOptions {
  /**
   * The path of the new directory
   *
   * @since 1.0.0
   */
  path: string;

  /**
   * The `Directory` to make the new directory in
   *
   * @since 1.0.0
   */
  directory?: Directory;

  /**
   * Whether to create any missing parent directories as well.
   *
   * @default false
   * @since 1.0.0
   */
  recursive?: boolean;
}

export interface RmdirOptions {
  /**
   * The path of the directory to remove
   *
   * @since 1.0.0
   */
  path: string;

  /**
   * The `Directory` to remove the directory from
   *
   * @since 1.0.0
   */
  directory?: Directory;

  /**
   * Whether to recursively remove the contents of the directory
   *
   * @default false
   * @since 1.0.0
   */
  recursive?: boolean;
}

export interface ReaddirOptions {
  /**
   * The path of the directory to read
   *
   * @since 1.0.0
   */
  path: string;

  /**
   * The `Directory` to list files from
   *
   * @since 1.0.0
   */
  directory?: Directory;
}

export interface GetUriOptions {
  /**
   * The path of the file to get the URI for
   *
   * @since 1.0.0
   */
  path: string;

  /**
   * The `Directory` to get the file under
   *
   * @since 1.0.0
   */
  directory: Directory;
}

export interface StatOptions {
  /**
   * The path of the file to get data about
   *
   * @since 1.0.0
   */
  path: string;

  /**
   * The `Directory` to get the file under
   *
   * @since 1.0.0
   */
  directory?: Directory;
}

export interface CopyOptions {
  /**
   * The existing file or directory
   *
   * @since 1.0.0
   */
  from: string;

  /**
   * The destination file or directory
   *
   * @since 1.0.0
   */
  to: string;

  /**
   * The `Directory` containing the existing file or directory
   *
   * @since 1.0.0
   */
  directory?: Directory;

  /**
   * The `Directory` containing the destination file or directory. If not supplied will use the 'directory'
   * parameter as the destination
   *
   * @since 1.0.0
   */
  toDirectory?: Directory;
}

export type RenameOptions = CopyOptions;

export interface ReadFileResult {
  /**
   * The representation of the data contained in the file
   *
   * Note: Blob is only available on Web. On native, the data is returned as a string.
   *
   * @since 1.0.0
   */
  data: string | Blob;
}

export interface WriteFileResult {
  /**
   * The uri where the file was written into
   *
   * @since 1.0.0
   */
  uri: string;
}

export interface ReaddirResult {
  /**
   * List of files and directories inside the directory
   *
   * @since 1.0.0
   */
  files: FileInfo[];
}

export interface FileInfo {
  /**
   * Name of the file or directory.
   */
  name: string;
  /**
   * Type of the file.
   *
   * @since 4.0.0
   */
  type: 'directory' | 'file';

  /**
   * Size of the file in bytes.
   *
   * @since 4.0.0
   */
  size: number;

  /**
   * Time of creation in milliseconds.
   *
   * It's not available on Android 7 and older devices.
   *
   * @since 4.0.0
   */
  ctime?: number;

  /**
   * Time of last modification in milliseconds.
   *
   * @since 4.0.0
   */
  mtime: number;

  /**
   * The uri of the file.
   *
   * @since 4.0.0
   */
  uri: string;
}

export interface GetUriResult {
  /**
   * The uri of the file
   *
   * @since 1.0.0
   */
  uri: string;
}

export interface StatResult {
  /**
   * Type of the file.
   *
   * @since 1.0.0
   */
  type: 'directory' | 'file';

  /**
   * Size of the file in bytes.
   *
   * @since 1.0.0
   */
  size: number;

  /**
   * Time of creation in milliseconds.
   *
   * It's not available on Android 7 and older devices.
   *
   * @since 1.0.0
   */
  ctime?: number;

  /**
   * Time of last modification in milliseconds.
   *
   * @since 1.0.0
   */
  mtime: number;

  /**
   * The uri of the file
   *
   * @since 1.0.0
   */
  uri: string;
}

export interface CopyResult {
  /**
   * The uri where the file was copied into
   *
   * @since 4.0.0
   */
  uri: string;
}

export interface DownloadFileOptions extends HttpOptions {
  /**
   * The path the downloaded file should be moved to.
   *
   * @since 5.1.0
   */
  path: string;
  /**
   * The directory to write the file to.
   * If this option is used, filePath can be a relative path rather than absolute.
   * The default is the `DATA` directory.
   *
   * @since 5.1.0
   */
  directory?: Directory;
  /**
   * An optional listener function to receive downloaded progress events.
   * If this option is used, progress event should be dispatched on every chunk received.
   * Chunks are throttled to every 100ms on Android/iOS to avoid slowdowns.
   *
   * @since 5.1.0
   */
  progress?: boolean;
  /**
   * Whether to create any missing parent directories.
   *
   * @default false
   * @since 5.1.2
   */
  recursive?: boolean;
}

export interface DownloadFileResult {
  /**
   * The path the file was downloaded to.
   *
   * @since 5.1.0
   */
  path?: string;
  /**
   * The blob data of the downloaded file.
   * This is only available on web.
   *
   * @since 5.1.0
   */
  blob?: Blob;
}
export interface ProgressStatus {
  /**
   * The url of the file being downloaded.
   *
   * @since 5.1.0
   */
  url: string;
  /**
   * The number of bytes downloaded so far.
   *
   * @since 5.1.0
   */
  bytes: number;
  /**
   * The total number of bytes to download for this file.
   *
   * @since 5.1.0
   */
  contentLength: number;
}

/**
 * A listener function that receives progress events.
 *
 * @since 5.1.0
 */
export type ProgressListener = (progress: ProgressStatus) => void;

export interface FilesystemPlugin {
  /**
   * Read a file from disk
   *
   * @since 1.0.0
   */
  readFile(options: ReadFileOptions): Promise<ReadFileResult>;

  /**
   * Write a file to disk in the specified location on device
   *
   * @since 1.0.0
   */
  writeFile(options: WriteFileOptions): Promise<WriteFileResult>;

  /**
   * Append to a file on disk in the specified location on device
   *
   * @since 1.0.0
   */
  appendFile(options: AppendFileOptions): Promise<void>;

  /**
   * Delete a file from disk
   *
   * @since 1.0.0
   */
  deleteFile(options: DeleteFileOptions): Promise<void>;

  /**
   * Create a directory.
   *
   * @since 1.0.0
   */
  mkdir(options: MkdirOptions): Promise<void>;

  /**
   * Remove a directory
   *
   * @since 1.0.0
   */
  rmdir(options: RmdirOptions): Promise<void>;

  /**
   * Return a list of files from the directory (not recursive)
   *
   * @since 1.0.0
   */
  readdir(options: ReaddirOptions): Promise<ReaddirResult>;

  /**
   * Return full File URI for a path and directory
   *
   * @since 1.0.0
   */
  getUri(options: GetUriOptions): Promise<GetUriResult>;

  /**
   * Return data about a file
   *
   * @since 1.0.0
   */
  stat(options: StatOptions): Promise<StatResult>;

  /**
   * Rename a file or directory
   *
   * @since 1.0.0
   */
  rename(options: RenameOptions): Promise<void>;

  /**
   * Copy a file or directory
   *
   * @since 1.0.0
   */
  copy(options: CopyOptions): Promise<CopyResult>;

  /**
   * Check read/write permissions.
   * Required on Android, only when using `Directory.Documents` or
   * `Directory.ExternalStorage`.
   *
   * @since 1.0.0
   */
  checkPermissions(): Promise<PermissionStatus>;

  /**
   * Request read/write permissions.
   * Required on Android, only when using `Directory.Documents` or
   * `Directory.ExternalStorage`.
   *
   * @since 1.0.0
   */
  requestPermissions(): Promise<PermissionStatus>;

  /**
   * Perform a http request to a server and download the file to the specified destination.
   *
   * @since 5.1.0
   */
  downloadFile(options: DownloadFileOptions): Promise<DownloadFileResult>;

  /**
   * Add a listener to file download progress events.
   *
   * @since 5.1.0
   */
  addListener(
    eventName: 'progress',
    listenerFunc: ProgressListener,
  ): Promise<PluginListenerHandle> & PluginListenerHandle;
}

/**
 * @deprecated Use `ReadFileOptions`.
 * @since 1.0.0
 */
export type FileReadOptions = ReadFileOptions;

/**
 * @deprecated Use `ReadFileResult`.
 * @since 1.0.0
 */
export type FileReadResult = ReadFileResult;

/**
 * @deprecated Use `WriteFileOptions`.
 * @since 1.0.0
 */
export type FileWriteOptions = WriteFileOptions;

/**
 * @deprecated Use `WriteFileResult`.
 * @since 1.0.0
 */
export type FileWriteResult = WriteFileResult;

/**
 * @deprecated Use `AppendFileOptions`.
 * @since 1.0.0
 */
export type FileAppendOptions = AppendFileOptions;

/**
 * @deprecated Use `DeleteFileOptions`.
 * @since 1.0.0
 */
export type FileDeleteOptions = DeleteFileOptions;

/**
 * @deprecated Use `Directory`.
 * @since 1.0.0
 */
export const FilesystemDirectory = Directory;

/**
 * @deprecated Use `Encoding`.
 * @since 1.0.0
 */
export const FilesystemEncoding = Encoding;
