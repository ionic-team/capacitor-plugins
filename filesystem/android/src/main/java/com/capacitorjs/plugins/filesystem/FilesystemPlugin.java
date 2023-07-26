package com.capacitorjs.plugins.filesystem;

import android.Manifest;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import com.capacitorjs.plugins.filesystem.exceptions.CopyFailedException;
import com.capacitorjs.plugins.filesystem.exceptions.DirectoryExistsException;
import com.capacitorjs.plugins.filesystem.exceptions.DirectoryNotFoundException;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Logger;
import com.getcapacitor.PermissionState;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;
import com.getcapacitor.plugin.util.HttpRequestHandler;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import org.json.JSONException;

@CapacitorPlugin(
    name = "Filesystem",
    permissions = {
        @Permission(
            strings = { Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE },
            alias = "publicStorage"
        )
    }
)
public class FilesystemPlugin extends Plugin {

    static final String PUBLIC_STORAGE = "publicStorage";
    private Filesystem implementation;

    @Override
    public void load() {
        implementation = new Filesystem(getContext());
    }

    private static final String PERMISSION_DENIED_ERROR = "Unable to do file operation, user denied permission request";

    @PluginMethod
    public void readFile(PluginCall call) {
        String path = call.getString("path");
        String directory = getDirectoryParameter(call);
        String encoding = call.getString("encoding");

        Charset charset = implementation.getEncoding(encoding);
        if (encoding != null && charset == null) {
            call.reject("Unsupported encoding provided: " + encoding);
            return;
        }

        if (isPublicDirectory(directory) && !isStoragePermissionGranted()) {
            requestAllPermissions(call, "permissionCallback");
        } else {
            try {
                String dataStr = implementation.readFile(path, directory, charset);
                JSObject ret = new JSObject();
                ret.putOpt("data", dataStr);
                call.resolve(ret);
            } catch (FileNotFoundException ex) {
                call.reject("File does not exist", ex);
            } catch (IOException ex) {
                call.reject("Unable to read file", ex);
            } catch (JSONException ex) {
                call.reject("Unable to return value for reading file", ex);
            }
        }
    }

    @PluginMethod
    public void writeFile(PluginCall call) {
        String path = call.getString("path");
        String data = call.getString("data");
        Boolean recursive = call.getBoolean("recursive", false);

        if (path == null) {
            Logger.error(getLogTag(), "No path or filename retrieved from call", null);
            call.reject("NO_PATH");
            return;
        }

        if (data == null) {
            Logger.error(getLogTag(), "No data retrieved from call", null);
            call.reject("NO_DATA");
            return;
        }

        String directory = getDirectoryParameter(call);
        if (directory != null) {
            if (isPublicDirectory(directory) && !isStoragePermissionGranted()) {
                requestAllPermissions(call, "permissionCallback");
            } else {
                // create directory because it might not exist
                File androidDir = implementation.getDirectory(directory);
                if (androidDir != null) {
                    if (androidDir.exists() || androidDir.mkdirs()) {
                        // path might include directories as well
                        File fileObject = new File(androidDir, path);
                        if (fileObject.getParentFile().exists() || (recursive && fileObject.getParentFile().mkdirs())) {
                            saveFile(call, fileObject, data);
                        } else {
                            call.reject("Parent folder doesn't exist");
                        }
                    } else {
                        Logger.error(getLogTag(), "Not able to create '" + directory + "'!", null);
                        call.reject("NOT_CREATED_DIR");
                    }
                } else {
                    Logger.error(getLogTag(), "Directory ID '" + directory + "' is not supported by plugin", null);
                    call.reject("INVALID_DIR");
                }
            }
        } else {
            // check file:// or no scheme uris
            Uri u = Uri.parse(path);
            if (u.getScheme() == null || u.getScheme().equals("file")) {
                File fileObject = new File(u.getPath());
                // do not know where the file is being store so checking the permission to be secure
                // TODO to prevent permission checking we need a property from the call
                if (!isStoragePermissionGranted()) {
                    requestAllPermissions(call, "permissionCallback");
                } else {
                    if (
                        fileObject.getParentFile() == null ||
                        fileObject.getParentFile().exists() ||
                        (recursive && fileObject.getParentFile().mkdirs())
                    ) {
                        saveFile(call, fileObject, data);
                    } else {
                        call.reject("Parent folder doesn't exist");
                    }
                }
            } else {
                call.reject(u.getScheme() + " scheme not supported");
            }
        }
    }

    private void saveFile(PluginCall call, File file, String data) {
        String encoding = call.getString("encoding");
        boolean append = call.getBoolean("append", false);

        Charset charset = implementation.getEncoding(encoding);
        if (encoding != null && charset == null) {
            call.reject("Unsupported encoding provided: " + encoding);
            return;
        }

        try {
            implementation.saveFile(file, data, charset, append);
            // update mediaStore index only if file was written to external storage
            if (isPublicDirectory(getDirectoryParameter(call))) {
                MediaScannerConnection.scanFile(getContext(), new String[] { file.getAbsolutePath() }, null, null);
            }
            Logger.debug(getLogTag(), "File '" + file.getAbsolutePath() + "' saved!");
            JSObject result = new JSObject();
            result.put("uri", Uri.fromFile(file).toString());
            call.resolve(result);
        } catch (IOException ex) {
            Logger.error(
                getLogTag(),
                "Creating file '" + file.getPath() + "' with charset '" + charset + "' failed. Error: " + ex.getMessage(),
                ex
            );
            call.reject("FILE_NOTCREATED");
        } catch (IllegalArgumentException ex) {
            call.reject("The supplied data is not valid base64 content.");
        }
    }

    @PluginMethod
    public void appendFile(PluginCall call) {
        try {
            call.getData().putOpt("append", true);
        } catch (JSONException ex) {}

        this.writeFile(call);
    }

    @PluginMethod
    public void deleteFile(PluginCall call) {
        String file = call.getString("path");
        String directory = getDirectoryParameter(call);
        if (isPublicDirectory(directory) && !isStoragePermissionGranted()) {
            requestAllPermissions(call, "permissionCallback");
        } else {
            try {
                boolean deleted = implementation.deleteFile(file, directory);
                if (!deleted) {
                    call.reject("Unable to delete file");
                } else {
                    call.resolve();
                }
            } catch (FileNotFoundException ex) {
                call.reject(ex.getMessage());
            }
        }
    }

    @PluginMethod
    public void mkdir(PluginCall call) {
        String path = call.getString("path");
        String directory = getDirectoryParameter(call);
        boolean recursive = call.getBoolean("recursive", false).booleanValue();
        if (isPublicDirectory(directory) && !isStoragePermissionGranted()) {
            requestAllPermissions(call, "permissionCallback");
        } else {
            try {
                boolean created = implementation.mkdir(path, directory, recursive);
                if (!created) {
                    call.reject("Unable to create directory, unknown reason");
                } else {
                    call.resolve();
                }
            } catch (DirectoryExistsException ex) {
                call.reject(ex.getMessage());
            }
        }
    }

    @PluginMethod
    public void rmdir(PluginCall call) {
        String path = call.getString("path");
        String directory = getDirectoryParameter(call);
        Boolean recursive = call.getBoolean("recursive", false);

        File fileObject = implementation.getFileObject(path, directory);

        if (isPublicDirectory(directory) && !isStoragePermissionGranted()) {
            requestAllPermissions(call, "permissionCallback");
        } else {
            if (!fileObject.exists()) {
                call.reject("Directory does not exist");
                return;
            }

            if (fileObject.isDirectory() && fileObject.listFiles().length != 0 && !recursive) {
                call.reject("Directory is not empty");
                return;
            }

            boolean deleted = false;

            try {
                implementation.deleteRecursively(fileObject);
                deleted = true;
            } catch (IOException ignored) {}

            if (!deleted) {
                call.reject("Unable to delete directory, unknown reason");
            } else {
                call.resolve();
            }
        }
    }

    @PluginMethod
    public void readdir(PluginCall call) {
        String path = call.getString("path");
        String directory = getDirectoryParameter(call);

        if (isPublicDirectory(directory) && !isStoragePermissionGranted()) {
            requestAllPermissions(call, "permissionCallback");
        } else {
            try {
                File[] files = implementation.readdir(path, directory);
                JSArray filesArray = new JSArray();
                if (files != null) {
                    for (var i = 0; i < files.length; i++) {
                        File fileObject = files[i];
                        JSObject data = new JSObject();
                        data.put("name", fileObject.getName());
                        data.put("type", fileObject.isDirectory() ? "directory" : "file");
                        data.put("size", fileObject.length());
                        data.put("mtime", fileObject.lastModified());
                        data.put("uri", Uri.fromFile(fileObject).toString());

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            try {
                                BasicFileAttributes attr = Files.readAttributes(fileObject.toPath(), BasicFileAttributes.class);

                                // use whichever is the oldest between creationTime and lastAccessTime
                                if (attr.creationTime().toMillis() < attr.lastAccessTime().toMillis()) {
                                    data.put("ctime", attr.creationTime().toMillis());
                                } else {
                                    data.put("ctime", attr.lastAccessTime().toMillis());
                                }
                            } catch (Exception ex) {}
                        } else {
                            data.put("ctime", null);
                        }
                        filesArray.put(data);
                    }

                    JSObject ret = new JSObject();
                    ret.put("files", filesArray);
                    call.resolve(ret);
                } else {
                    call.reject("Unable to read directory");
                }
            } catch (DirectoryNotFoundException ex) {
                call.reject(ex.getMessage());
            }
        }
    }

    @PluginMethod
    public void getUri(PluginCall call) {
        String path = call.getString("path");
        String directory = getDirectoryParameter(call);

        File fileObject = implementation.getFileObject(path, directory);

        if (isPublicDirectory(directory) && !isStoragePermissionGranted()) {
            requestAllPermissions(call, "permissionCallback");
        } else {
            JSObject data = new JSObject();
            data.put("uri", Uri.fromFile(fileObject).toString());
            call.resolve(data);
        }
    }

    @PluginMethod
    public void stat(PluginCall call) {
        String path = call.getString("path");
        String directory = getDirectoryParameter(call);

        File fileObject = implementation.getFileObject(path, directory);

        if (isPublicDirectory(directory) && !isStoragePermissionGranted()) {
            requestAllPermissions(call, "permissionCallback");
        } else {
            if (!fileObject.exists()) {
                call.reject("File does not exist");
                return;
            }

            JSObject data = new JSObject();
            data.put("type", fileObject.isDirectory() ? "directory" : "file");
            data.put("size", fileObject.length());
            data.put("mtime", fileObject.lastModified());
            data.put("uri", Uri.fromFile(fileObject).toString());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    BasicFileAttributes attr = Files.readAttributes(fileObject.toPath(), BasicFileAttributes.class);

                    // use whichever is the oldest between creationTime and lastAccessTime
                    if (attr.creationTime().toMillis() < attr.lastAccessTime().toMillis()) {
                        data.put("ctime", attr.creationTime().toMillis());
                    } else {
                        data.put("ctime", attr.lastAccessTime().toMillis());
                    }
                } catch (Exception ex) {}
            } else {
                data.put("ctime", null);
            }

            call.resolve(data);
        }
    }

    @PluginMethod
    public void rename(PluginCall call) {
        this._copy(call, true);
    }

    @PluginMethod
    public void copy(PluginCall call) {
        this._copy(call, false);
    }

    @PluginMethod
    public void downloadFile(PluginCall call) {
        try {
            String directory = call.getString("directory", Environment.DIRECTORY_DOWNLOADS);

            if (isPublicDirectory(directory) && !isStoragePermissionGranted()) {
                requestAllPermissions(call, "permissionCallback");
            } else {
                HttpRequestHandler.ProgressEmitter emitter = (bytes, contentLength) -> {
                    JSObject ret = new JSObject();
                    ret.put("url", call.getString("url"));
                    ret.put("bytes", bytes);
                    ret.put("contentLength", contentLength);

                    notifyListeners("progress", ret);
                };

                JSObject response = implementation.downloadFile(call, bridge, emitter);
                call.resolve(response);
            }
        } catch (Exception ex) {
            call.reject("Error downloading file: " + ex.getLocalizedMessage(), ex);
        }
    }

    private void _copy(PluginCall call, Boolean doRename) {
        String from = call.getString("from");
        String to = call.getString("to");
        String directory = call.getString("directory");
        String toDirectory = call.getString("toDirectory");

        if (from == null || from.isEmpty() || to == null || to.isEmpty()) {
            call.reject("Both to and from must be provided");
            return;
        }
        if (isPublicDirectory(directory) || isPublicDirectory(toDirectory)) {
            if (!isStoragePermissionGranted()) {
                requestAllPermissions(call, "permissionCallback");
                return;
            }
        }
        try {
            File file = implementation.copy(from, directory, to, toDirectory, doRename);
            if (!doRename) {
                JSObject result = new JSObject();
                result.put("uri", Uri.fromFile(file).toString());
                call.resolve(result);
            } else {
                call.resolve();
            }
        } catch (CopyFailedException ex) {
            call.reject(ex.getMessage());
        } catch (IOException ex) {
            call.reject("Unable to perform action: " + ex.getLocalizedMessage());
        }
    }

    @PluginMethod
    public void checkPermissions(PluginCall call) {
        if (isStoragePermissionGranted()) {
            JSObject permissionsResultJSON = new JSObject();
            permissionsResultJSON.put(PUBLIC_STORAGE, "granted");
            call.resolve(permissionsResultJSON);
        } else {
            super.checkPermissions(call);
        }
    }

    @PluginMethod
    public void requestPermissions(PluginCall call) {
        if (isStoragePermissionGranted()) {
            JSObject permissionsResultJSON = new JSObject();
            permissionsResultJSON.put(PUBLIC_STORAGE, "granted");
            call.resolve(permissionsResultJSON);
        } else {
            requestPermissionForAlias(PUBLIC_STORAGE, call, "permissionCallback");
        }
    }

    @PermissionCallback
    private void permissionCallback(PluginCall call) {
        if (!isStoragePermissionGranted()) {
            Logger.debug(getLogTag(), "User denied storage permission");
            call.reject(PERMISSION_DENIED_ERROR);
            return;
        }

        switch (call.getMethodName()) {
            case "appendFile":
            case "writeFile":
                writeFile(call);
                break;
            case "deleteFile":
                deleteFile(call);
                break;
            case "mkdir":
                mkdir(call);
                break;
            case "rmdir":
                rmdir(call);
                break;
            case "rename":
                rename(call);
                break;
            case "copy":
                copy(call);
                break;
            case "readFile":
                readFile(call);
                break;
            case "readdir":
                readdir(call);
                break;
            case "getUri":
                getUri(call);
                break;
            case "stat":
                stat(call);
                break;
            case "downloadFile":
                downloadFile(call);
                break;
        }
    }

    /**
     * Checks the the given permission is granted or not
     * @return Returns true if the app is running on Android 30 or newer or if the permission is already granted
     * or false if it is denied.
     */
    private boolean isStoragePermissionGranted() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R || getPermissionState(PUBLIC_STORAGE) == PermissionState.GRANTED;
    }

    /**
     * Reads the directory parameter from the plugin call
     * @param call the plugin call
     */
    private String getDirectoryParameter(PluginCall call) {
        return call.getString("directory");
    }

    /**
     * True if the given directory string is a public storage directory, which is accessible by the user or other apps.
     * @param directory the directory string.
     */
    private boolean isPublicDirectory(String directory) {
        return "DOCUMENTS".equals(directory) || "EXTERNAL_STORAGE".equals(directory);
    }
}
