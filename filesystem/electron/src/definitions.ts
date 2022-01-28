import type {
  AppendFileOptions,
  CopyOptions,
  DeleteFileOptions,
  GetUriOptions,
  GetUriResult,
  MkdirOptions,
  ReaddirOptions,
  ReaddirResult,
  ReadFileOptions,
  ReadFileResult,
  RenameOptions,
  RmdirOptions,
  StatOptions,
  StatResult,
  WriteFileOptions,
  WriteFileResult,
  PermissionStatus,
} from '../../src/definitions';

export interface FilesystemPlugin {
  /**
   * Read a file from disk
   *
   * @since 1.1.1
   */
  readFile(options: ReadFileOptions): Promise<ReadFileResult>;

  /**
   * Write a file to disk in the specified location on device
   *
   * @since 1.1.1
   */
  writeFile(options: WriteFileOptions): Promise<WriteFileResult>;

  /**
   * Append to a file on disk in the specified location on device
   *
   * @since 1.1.1
   */
  appendFile(options: AppendFileOptions): Promise<void>;

  /**
   * Delete a file from disk
   *
   * @since 1.1.1
   */
  deleteFile(options: DeleteFileOptions): Promise<void>;

  /**
   * Create a directory.
   *
   * @since 1.1.1
   */
  mkdir(options: MkdirOptions): Promise<void>;

  /**
   * Remove a directory
   *
   * @since 1.1.1
   */
  rmdir(options: RmdirOptions): Promise<void>;

  /**
   * Return a list of files from the directory (not recursive)
   *
   * @since 1.1.1
   */
  readdir(options: ReaddirOptions): Promise<ReaddirResult>;

  /**
   * Return full File URI for a path and directory
   *
   * @since 1.1.1
   */
  getUri(options: GetUriOptions): Promise<GetUriResult>;

  /**
   * Return data about a file
   *
   * @since 1.1.1
   */
  stat(options: StatOptions): Promise<StatResult>;

  /**
   * Rename a file or directory
   *
   * @since 1.1.1
   */
  rename(options: RenameOptions): Promise<void>;

  /**
   * Copy a file or directory
   *
   * @since 1.1.1
   */
  copy(options: CopyOptions): Promise<void>;

  /**
   * Check read/write permissions.
   *
   * @since 1.1.1
   */
  checkPermissions(): Promise<PermissionStatus>;

  /**
   * Request read/write permissions.
   *
   * @since 1.1.1
   */
  requestPermissions(): Promise<PermissionStatus>;
}
