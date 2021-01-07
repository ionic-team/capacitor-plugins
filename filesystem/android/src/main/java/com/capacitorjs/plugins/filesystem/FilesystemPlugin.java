package com.capacitorjs.plugins.filesystem;

import android.Manifest;
import android.media.MediaScannerConnection;
import android.net.Uri;
import com.capacitorjs.plugins.filesystem.exceptions.CopyFailedException;
import com.capacitorjs.plugins.filesystem.exceptions.DirectoryExistsException;
import com.capacitorjs.plugins.filesystem.exceptions.DirectoryNotFoundException;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Logger;
import com.getcapacitor.PermissionResponse;
import com.getcapacitor.PermissionState;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import java.io.*;
import java.nio.charset.Charset;
import java.util.Map;
import org.json.JSONException;

@CapacitorPlugin(
    name = "Filesystem",
    permissions = {
        @Permission(strings = { Manifest.permission.READ_EXTERNAL_STORAGE }, alias = FilesystemPlugin.READ_STORAGE),
        @Permission(strings = { Manifest.permission.WRITE_EXTERNAL_STORAGE }, alias = FilesystemPlugin.WRITE_STORAGE)
    }
)
public class FilesystemPlugin extends Plugin {

    static final String READ_STORAGE = "readStorage";
    static final String WRITE_STORAGE = "writeStorage";

    private Filesystem implementation;

    @Override
    public void load() {
        implementation = new Filesystem(getContext());
    }

    private static final String PERMISSION_DENIED_ERROR = "Unable to do file operation, user denied permission request";

    @PluginMethod(permissionCallback = "completeReadOperations")
    public void readFile(PluginCall call) {
        String path = call.getString("path");
        String directory = getDirectoryParameter(call);
        String encoding = call.getString("encoding");

        Charset charset = implementation.getEncoding(encoding);
        if (encoding != null && charset == null) {
            call.reject("Unsupported encoding provided: " + encoding);
            return;
        }

        if (!isPublicDirectory(directory) && !isStoragePermissionGranted(READ_STORAGE)) {
            requestPermissionForAlias(READ_STORAGE, call);
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

    @PluginMethod(permissionCallback = "completeWriteOperations")
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
            if (!isPublicDirectory(directory) && !isStoragePermissionGranted(WRITE_STORAGE)) {
                requestPermissionForAlias(WRITE_STORAGE, call);
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
            // check if file://
            Uri u = Uri.parse(path);
            if ("file".equals(u.getScheme())) {
                File fileObject = new File(u.getPath());
                // do not know where the file is being store so checking the permission to be secure
                // TODO to prevent permission checking we need a property from the call
                if (!isStoragePermissionGranted(WRITE_STORAGE)) {
                    requestPermissionForAlias(WRITE_STORAGE, call);
                } else {
                    if (fileObject.getParentFile().exists() || (recursive && fileObject.getParentFile().mkdirs())) {
                        saveFile(call, fileObject, data);
                    } else {
                        call.reject("Parent folder doesn't exist");
                    }
                }
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
        }
    }

    @PluginMethod(permissionCallback = "completeWriteOperations")
    public void appendFile(PluginCall call) {
        try {
            call.getData().putOpt("append", true);
        } catch (JSONException ex) {}

        this.writeFile(call);
    }

    @PluginMethod(permissionCallback = "completeWriteOperations")
    public void deleteFile(PluginCall call) {
        String file = call.getString("path");
        String directory = getDirectoryParameter(call);
        if (!isPublicDirectory(directory) && !isStoragePermissionGranted(WRITE_STORAGE)) {
            requestPermissionForAlias(WRITE_STORAGE, call);
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

    @PluginMethod(permissionCallback = "completeWriteOperations")
    public void mkdir(PluginCall call) {
        String path = call.getString("path");
        String directory = getDirectoryParameter(call);
        boolean recursive = call.getBoolean("recursive", false).booleanValue();
        if (!isPublicDirectory(directory) && !isStoragePermissionGranted(WRITE_STORAGE)) {
            requestPermissionForAlias(WRITE_STORAGE, call);
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

    @PluginMethod(permissionCallback = "completeWriteOperations")
    public void rmdir(PluginCall call) {
        String path = call.getString("path");
        String directory = getDirectoryParameter(call);
        Boolean recursive = call.getBoolean("recursive", false);

        File fileObject = implementation.getFileObject(path, directory);

        if (!isPublicDirectory(directory) && !isStoragePermissionGranted(WRITE_STORAGE)) {
            requestPermissionForAlias(WRITE_STORAGE, call);
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

    @PluginMethod(permissionCallback = "completeReadOperations")
    public void readdir(PluginCall call) {
        String path = call.getString("path");
        String directory = getDirectoryParameter(call);

        if (!isPublicDirectory(directory) && !isStoragePermissionGranted(READ_STORAGE)) {
            requestPermissionForAlias(READ_STORAGE, call);
        } else {
            try {
                String[] files = implementation.readdir(path, directory);
                if (files != null) {
                    JSObject ret = new JSObject();
                    ret.put("files", JSArray.from(files));
                    call.resolve(ret);
                } else {
                    call.reject("Unable to read directory");
                }
            } catch (DirectoryNotFoundException ex) {
                call.reject(ex.getMessage());
            }
        }
    }

    @PluginMethod(permissionCallback = "completeReadOperations")
    public void getUri(PluginCall call) {
        String path = call.getString("path");
        String directory = getDirectoryParameter(call);

        File fileObject = implementation.getFileObject(path, directory);

        if (!isPublicDirectory(directory) && !isStoragePermissionGranted(READ_STORAGE)) {
            requestPermissionForAlias(READ_STORAGE, call);
        } else {
            JSObject data = new JSObject();
            data.put("uri", Uri.fromFile(fileObject).toString());
            call.resolve(data);
        }
    }

    @PluginMethod(permissionCallback = "completeReadOperations")
    public void stat(PluginCall call) {
        String path = call.getString("path");
        String directory = getDirectoryParameter(call);

        File fileObject = implementation.getFileObject(path, directory);

        if (!isPublicDirectory(directory) && !isStoragePermissionGranted(READ_STORAGE)) {
            requestPermissionForAlias(READ_STORAGE, call);
        } else {
            if (!fileObject.exists()) {
                call.reject("File does not exist");
                return;
            }

            JSObject data = new JSObject();
            data.put("type", fileObject.isDirectory() ? "directory" : "file");
            data.put("size", fileObject.length());
            data.put("ctime", null);
            data.put("mtime", fileObject.lastModified());
            data.put("uri", Uri.fromFile(fileObject).toString());
            call.resolve(data);
        }
    }

    @PluginMethod(permissionCallback = "completeWriteOperations")
    public void rename(PluginCall call) {
        this._copy(call, true);
    }

    @PluginMethod(permissionCallback = "completeWriteOperations")
    public void copy(PluginCall call) {
        this._copy(call, false);
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
            if (!isStoragePermissionGranted(WRITE_STORAGE)) {
                requestPermissionForAlias(WRITE_STORAGE, call);
                return;
            }
        }
        try {
            implementation.copy(from, directory, to, toDirectory, doRename);
        } catch (CopyFailedException ex) {
            call.reject(ex.getMessage());
        } catch (IOException ex) {
            call.reject("Unable to perform action: " + ex.getLocalizedMessage());
        }
    }

    private void completeReadOperations(PluginCall call, Map<String, PermissionState> status) {
        if (status.get(READ_STORAGE) != PermissionState.GRANTED) {
            Logger.debug(getLogTag(), "User denied read storage permission");
            call.reject(PERMISSION_DENIED_ERROR);
            return;
        }

        switch (call.getMethodName()) {
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
        }
    }

    private void completeWriteOperations(PluginCall call, Map<String, PermissionState> status) {
        if (status.get(WRITE_STORAGE) != PermissionState.GRANTED) {
            Logger.debug(getLogTag(), "User denied write storage permission");
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
        }
    }

    /**
     * Checks the the given permission is granted or not
     * @param alias the permission alias "readStorage" or "writeStorage"
     * @return Returns true if the permission is granted and false if it is denied.
     */
    private boolean isStoragePermissionGranted(String alias) {
        return getPermissionStates().get(alias) == PermissionState.GRANTED;
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
