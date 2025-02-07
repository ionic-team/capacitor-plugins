import Foundation
import Capacitor
import IONFilesystemLib

typealias FileService = any IONFILEDirectoryManager & IONFILEFileManager

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(FilesystemPlugin)
public class FilesystemPlugin: CAPPlugin, CAPBridgedPlugin {
    public let identifier = "FilesystemPlugin"
    public let jsName = "Filesystem"
    public let pluginMethods: [CAPPluginMethod] = [
        CAPPluginMethod(name: "readFile", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "writeFile", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "appendFile", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "deleteFile", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "mkdir", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "rmdir", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "readdir", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "getUri", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "stat", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "rename", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "copy", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "checkPermissions", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "requestPermissions", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "downloadFile", returnType: CAPPluginReturnPromise)
    ]
    
    private var fileService: FileService?

    public override func load() {
        self.fileService = IONFILEManager()
    }

    func getService() -> Result<FileService, FilesystemError> {
        if fileService == nil { load() }
        return fileService.map(Result.success) ?? .failure(.bridgeNotInitialised)
    }

    override public func checkPermissions(_ call: CAPPluginCall) {
        call.handlePermissionSuccess()
    }

    override public func requestPermissions(_ call: CAPPluginCall) {
        call.handlePermissionSuccess()
    }
}

// MARK: - Public API Methods
private extension FilesystemPlugin {
    /**
     * Read a file from the filesystem.
     */
    @objc func readFile(_ call: CAPPluginCall) {
        let encoding = call.getEncoding(Constants.MethodParameter.encoding)
        performSinglePathOperation(call) {
            .read(url: $0, encoding: encoding)
        }
    }

    /**
     * Write a file to the filesystem.
     */
    @objc func writeFile(_ call: CAPPluginCall) {
        guard let data = call.getString(Constants.MethodParameter.data) else {
            return call.handleError(.invalidDataParameter)
        }
        guard let encodingMapper = call.getEncodingMapper(usingValue: data) else {
            return call.handleError(.invalidDataEncodingCombination(method: .writeFile))
        }
        let recursive = call.getBool(Constants.MethodParameter.recursive, false)

        performSinglePathOperation(call) {
            .write(url: $0, encodingMapper: encodingMapper, recursive: recursive)
        }
    }

    /**
     * Append to a file.
     */
    @objc func appendFile(_ call: CAPPluginCall) {
        guard let data = call.getString(Constants.MethodParameter.data) else {
            return call.handleError(.invalidDataParameter)
        }
        guard let encodingMapper = call.getEncodingMapper(usingValue: data) else {
            return call.handleError(.invalidDataEncodingCombination(method: .appendFile))
        }
        let recursive = call.getBool(Constants.MethodParameter.recursive, false)

        performSinglePathOperation(call) {
            .append(url: $0, encodingMapper: encodingMapper, recursive: recursive)
        }
    }

    /**
     * Delete a file.
     */
    @objc func deleteFile(_ call: CAPPluginCall) {
        performSinglePathOperation(call) {
            .delete(url: $0)
        }
    }

    /**
     * Make a new directory, optionally creating parent folders first.
     */
    @objc func mkdir(_ call: CAPPluginCall) {
        let recursive = call.getBool(Constants.MethodParameter.recursive, false)

        performSinglePathOperation(call) {
            .mkdir(url: $0,recursive: recursive)
        }
    }

    /**
     * Remove a directory.
     */
    @objc func rmdir(_ call: CAPPluginCall) {
        let recursive = call.getBool(Constants.MethodParameter.recursive, false)

        performSinglePathOperation(call) {
            .rmdir(url: $0, recursive: recursive)
        }
    }

    /**
     * Read the contents of a directory.
     */
    @objc func readdir(_ call: CAPPluginCall) {
        performSinglePathOperation(call) {
            .readdir(url: $0)
        }
    }

    @objc func stat(_ call: CAPPluginCall) {
        performSinglePathOperation(call) {
            .stat(url: $0)
        }
    }

    @objc func getUri(_ call: CAPPluginCall) {
        performSinglePathOperation(call) {
            .getUri(url: $0)
        }
    }

    /**
     * Rename a file or directory.
     */
    @objc func rename(_ call: CAPPluginCall) {
        performDualPathOperation(call) {
            .rename(source: $0, destination: $1)
        }
    }

    /**
     * Copy a file or directory.
     */
    @objc func copy(_ call: CAPPluginCall) {
        performDualPathOperation(call) {
            .copy(source: $0, destination: $1)
        }
    }

    @objc func downloadFile(_ call: CAPPluginCall) {
        //        guard let url = call.getString("url") else { return call.reject("Must provide a URL") }
        //        let progressEmitter: Filesystem.ProgressEmitter = { bytes, contentLength in
        //            self.notifyListeners("progress", data: [
        //                "url": url,
        //                "bytes": bytes,
        //                "contentLength": contentLength
        //            ])
        //        }
        //
        //        do {
        //            try implementation.downloadFile(call: call, emitter: progressEmitter, config: bridge?.config)
        //        } catch let error {
        //            call.reject(error.localizedDescription)
        //        }
    }
}

// MARK: - Operation Execution
private extension FilesystemPlugin {
    func performSinglePathOperation(_ call: CAPPluginCall, operationBuilder: (URL) -> FilesystemOperation) {
        executeOperation(call) { service in
            FilesystemLocationResolver(service: service)
                .resolveSinglePath(from: call)
                .map { operationBuilder($0) }
        }
    }

    func performDualPathOperation(_ call: CAPPluginCall, operationBuilder: (URL, URL) -> FilesystemOperation) {
        executeOperation(call) { service in
            FilesystemLocationResolver(service: service)
                .resolveDualPaths(from: call)
                .map { operationBuilder($0.source, $0.destination) }
        }
    }

    func executeOperation(_ call: CAPPluginCall, operationProvider: (FileService) -> Result<FilesystemOperation, FilesystemError>) {
        switch getService() {
        case .success(let service):
            switch operationProvider(service) {
            case .success(let operation):
                let executor = FilesystemOperationExecutor(service: service)
                executor.execute(operation, call)
            case .failure(let error):
                call.handleError(error)
            }
        case .failure(let error):
            call.handleError(error)
        }
    }
}
