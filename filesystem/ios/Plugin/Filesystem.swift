import Foundation
import Capacitor

@objc public class Filesystem: NSObject {

    public enum FilesystemError: LocalizedError {
        case noParentFolder, noSave, failEncode, noAppend, notEmpty

        public var errorDescription: String? {
            switch self {
            case .noParentFolder:
                return "Parent folder doesn't exist"
            case .noSave:
                return "Unable to save file"
            case .failEncode:
                return "Unable to encode data to utf-8"
            case .noAppend:
                return "Unable to append file"
            case .notEmpty:
                return "Folder is not empty"
            }
        }
    }

    public typealias ProgressEmitter = (_ bytes: Int64, _ contentLength: Int64) -> Void

    public func readFile(at fileUrl: URL, with encoding: String?) throws -> String {
        if encoding != nil {
            let data = try String(contentsOf: fileUrl, encoding: .utf8)
            return data
        } else {
            let data = try Data(contentsOf: fileUrl)
            return data.base64EncodedString()
        }
    }

    public func writeFile(at fileUrl: URL, with data: String, recursive: Bool, with encoding: String?) throws -> String {
        if !FileManager.default.fileExists(atPath: fileUrl.deletingLastPathComponent().path) {
            if recursive {
                try FileManager.default.createDirectory(at: fileUrl.deletingLastPathComponent(), withIntermediateDirectories: recursive, attributes: nil)
            } else {
                throw FilesystemError.noParentFolder
            }
        }
        if encoding != nil {
            try data.write(to: fileUrl, atomically: false, encoding: .utf8)
        } else {
            if let base64Data = Data.capacitor.data(base64EncodedOrDataUrl: data) {
                try base64Data.write(to: fileUrl)
            } else {
                throw FilesystemError.noSave
            }
        }
        return fileUrl.absoluteString
    }

    @objc public func appendFile(at fileUrl: URL, with data: String, recursive: Bool, with encoding: String?) throws {
        if FileManager.default.fileExists(atPath: fileUrl.path) {
            let fileHandle = try FileHandle.init(forWritingTo: fileUrl)
            var writeData: Data?
            if encoding != nil {
                guard let userData = data.data(using: .utf8) else {
                    throw FilesystemError.failEncode
                }
                writeData = userData
            } else {
                if let base64Data = Data.capacitor.data(base64EncodedOrDataUrl: data) {
                    writeData = base64Data
                } else {
                    throw FilesystemError.noAppend
                }
            }
            defer {
                fileHandle.closeFile()
            }
            fileHandle.seekToEndOfFile()
            fileHandle.write(writeData!)
        } else {
            _ = try writeFile(at: fileUrl, with: data, recursive: recursive, with: encoding)
        }
    }

    @objc func deleteFile(at fileUrl: URL) throws {
        if FileManager.default.fileExists(atPath: fileUrl.path) {
            try FileManager.default.removeItem(atPath: fileUrl.path)
        }
    }

    @objc public func mkdir(at fileUrl: URL, recursive: Bool) throws {
        try FileManager.default.createDirectory(at: fileUrl, withIntermediateDirectories: recursive, attributes: nil)
    }

    @objc public func rmdir(at fileUrl: URL, recursive: Bool) throws {
        let directoryContents = try FileManager.default.contentsOfDirectory(at: fileUrl, includingPropertiesForKeys: nil, options: [])
        if directoryContents.count != 0 && !recursive {
            throw FilesystemError.notEmpty
        }
        try FileManager.default.removeItem(at: fileUrl)
    }

    public func readdir(at fileUrl: URL) throws -> [URL] {
        return try FileManager.default.contentsOfDirectory(at: fileUrl, includingPropertiesForKeys: nil, options: [])
    }

    func stat(at fileUrl: URL) throws -> [FileAttributeKey: Any] {
        return try FileManager.default.attributesOfItem(atPath: fileUrl.path)
    }

    func getType(from attr: [FileAttributeKey: Any]) -> String {
        let fileType = attr[.type] as? String ?? ""
        if fileType == "NSFileTypeDirectory" {
            return "directory"
        } else {
            return "file"
        }
    }

    @objc public func rename(at srcURL: URL, to dstURL: URL) throws {
        try _copy(at: srcURL, to: dstURL, doRename: true)
    }

    @objc public func copy(at srcURL: URL, to dstURL: URL) throws {
        try _copy(at: srcURL, to: dstURL, doRename: false)
    }

    /**
     * Copy or rename a file or directory.
     */
    private func _copy(at srcURL: URL, to dstURL: URL, doRename: Bool) throws {
        if srcURL == dstURL {
            return
        }
        var isDir: ObjCBool = false
        if FileManager.default.fileExists(atPath: dstURL.path, isDirectory: &isDir) {
            if !isDir.boolValue {
                try? FileManager.default.removeItem(at: dstURL)
            }
        }

        if doRename {
            try FileManager.default.moveItem(at: srcURL, to: dstURL)
        } else {
            try FileManager.default.copyItem(at: srcURL, to: dstURL)
        }

    }

    /**
     * Get the SearchPathDirectory corresponding to the JS string
     */
    public func getDirectory(directory: String?) -> FileManager.SearchPathDirectory? {
        if let directory = directory {
            switch directory {
            case "CACHE":
                return .cachesDirectory
            case "LIBRARY":
                return .libraryDirectory
            default:
                return .documentDirectory
            }
        }
        return nil
    }

    /**
     * Get the URL for this file, supporting file:// paths and
     * files with directory mappings.
     */
    @objc public func getFileUrl(at path: String, in directory: String?) -> URL? {
        if let directory = getDirectory(directory: directory) {
            guard let dir = FileManager.default.urls(for: directory, in: .userDomainMask).first else {
                return nil
            }
            if !path.isEmpty {
                return dir.appendingPathComponent(path)
            }
            return dir
        } else {
            return URL(string: path)
        }
    }

    // swiftlint:disable function_body_length
    @objc public func downloadFile(call: CAPPluginCall, emitter: @escaping ProgressEmitter, config: InstanceConfiguration?) throws {
        let directory = call.getString("directory", "DOCUMENTS")
        guard let path = call.getString("path") else {
            call.reject("Invalid file path")
            return
        }

        func handleDownload(downloadLocation: URL?, response: URLResponse?, error: Error?) {
            if let error = error {
                CAPLog.print("Error on download file", String(describing: downloadLocation), String(describing: response), String(describing: error))
                call.reject(error.localizedDescription, "DOWNLOAD", error, nil)
                return
            }

            if let httpResponse = response as? HTTPURLResponse {
                HttpRequestHandler.setCookiesFromResponse(httpResponse, config)
            }

            guard let location = downloadLocation else {
                call.reject("Unable to get file after downloading")
                return
            }

            let fileManager = FileManager.default

            if let foundDir = getDirectory(directory: directory) {
                let dir = fileManager.urls(for: foundDir, in: .userDomainMask).first

                do {
                    let dest = dir!.appendingPathComponent(path)
                    CAPLog.print("Attempting to write to file destination: \(dest.absoluteString)")

                    if !FileManager.default.fileExists(atPath: dest.deletingLastPathComponent().absoluteString) {
                        try FileManager.default.createDirectory(at: dest.deletingLastPathComponent(), withIntermediateDirectories: true, attributes: nil)
                    }

                    if FileManager.default.fileExists(atPath: dest.relativePath) {
                        do {
                            CAPLog.print("File already exists. Attempting to remove file before writing.")
                            try fileManager.removeItem(at: dest)
                        } catch let error {
                            call.reject("Unable to remove existing file: \(error.localizedDescription)")
                            return
                        }
                    }

                    try fileManager.moveItem(at: location, to: dest)
                    CAPLog.print("Downloaded file successfully! \(dest.absoluteString)")
                    call.resolve(["path": dest.absoluteString])
                } catch let error {
                    call.reject("Unable to download file: \(error.localizedDescription)", "DOWNLOAD", error)
                    return
                }
            } else {
                call.reject("Unable to download file. Couldn't find directory \(directory)")
            }
        }

        guard var urlString = call.getString("url") else { throw URLError(.badURL) }
        let method = call.getString("method", "GET")

        let headers = (call.getObject("headers") ?? [:]) as [String: Any]
        let params = (call.getObject("params") ?? [:]) as [String: Any]
        let responseType = call.getString("responseType", "text")
        let connectTimeout = call.getDouble("connectTimeout")
        let readTimeout = call.getDouble("readTimeout")

        if urlString == urlString.removingPercentEncoding {
            guard let encodedUrlString = urlString.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed)  else { throw URLError(.badURL) }
            urlString = encodedUrlString
        }

        let progress = call.getBool("progress", false)

        let request = try HttpRequestHandler.CapacitorHttpRequestBuilder()
            .setUrl(urlString)
            .setMethod(method)
            .setUrlParams(params)
            .openConnection()
            .build()

        request.setRequestHeaders(headers)

        // Timeouts in iOS are in seconds. So read the value in millis and divide by 1000
        let timeout = (connectTimeout ?? readTimeout ?? 600000.0) / 1000.0
        request.setTimeout(timeout)

        if let data = call.options["data"] as? JSValue {
            do {
                try request.setRequestBody(data)
            } catch {
                // Explicitly reject if the http request body was not set successfully,
                // so as to not send a known malformed request, and to provide the developer with additional context.
                call.reject(error.localizedDescription, (error as NSError).domain, error, nil)
                return
            }
        }

        var session: URLSession!
        var task: URLSessionDownloadTask!
        let urlRequest = request.getUrlRequest()

        if progress {
            class ProgressDelegate: NSObject, URLSessionDataDelegate, URLSessionDownloadDelegate {
                private var handler: (URL?, URLResponse?, Error?) -> Void
                private var downloadLocation: URL?
                private var response: URLResponse?
                private var emitter: (Int64, Int64) -> Void
                private var lastEmitTimestamp: TimeInterval = 0.0

                init(downloadHandler: @escaping (URL?, URLResponse?, Error?) -> Void, progressEmitter: @escaping (Int64, Int64) -> Void) {
                    handler = downloadHandler
                    emitter = progressEmitter
                }

                func urlSession(_ session: URLSession, downloadTask: URLSessionDownloadTask, didWriteData bytesWritten: Int64, totalBytesWritten: Int64, totalBytesExpectedToWrite: Int64) {
                    let currentTimestamp = Date().timeIntervalSince1970
                    let timeElapsed = currentTimestamp - lastEmitTimestamp

                    if totalBytesExpectedToWrite > 0 {
                        if timeElapsed >= 0.1 {
                            emitter(totalBytesWritten, totalBytesExpectedToWrite)
                            lastEmitTimestamp = currentTimestamp
                        }
                    } else {
                        emitter(totalBytesWritten, 0)
                        lastEmitTimestamp = currentTimestamp
                    }
                }

                func urlSession(_ session: URLSession, downloadTask: URLSessionDownloadTask, didFinishDownloadingTo location: URL) {
                    downloadLocation = location
                    handler(downloadLocation, downloadTask.response, downloadTask.error)
                }

                func urlSession(_ session: URLSession, task: URLSessionTask, didCompleteWithError error: Error?) {
                    if error != nil {
                        handler(downloadLocation, task.response, error)
                    }
                }
            }

            let progressDelegate = ProgressDelegate(downloadHandler: handleDownload, progressEmitter: emitter)
            session = URLSession(configuration: .default, delegate: progressDelegate, delegateQueue: nil)
            task = session.downloadTask(with: urlRequest)
        } else {
            task = URLSession.shared.downloadTask(with: urlRequest, completionHandler: handleDownload)
        }

        task.resume()
    }
    // swiftlint:enable function_body_length
}
