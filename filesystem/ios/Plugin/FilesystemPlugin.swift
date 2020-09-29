import Foundation
import Capacitor

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(FilesystemPlugin)
public class FilesystemPlugin: CAPPlugin {
    private let implementation = Filesystem()
    let defaultDirectory = "DOCUMENTS"

    /**
     * Read a file from the filesystem.
     */
    @objc func readFile(_ call: CAPPluginCall) {
        let encoding = call.getString("encoding")

        guard let file = call.getString("path") else {
            handleError(call, "path must be provided and must be a string.")
            return
        }
        let directory = call.getString("directory") ?? defaultDirectory

        guard let fileUrl = implementation.getFileUrl(at: file, in: directory) else {
            handleError(call, "Invalid path")
            return
        }
        do {
            let data = try implementation.readFile(at: fileUrl, with: encoding)
            call.resolve([
                "data": data
            ])
        } catch let error as NSError {
            handleError(call, error.localizedDescription, error)
        }
    }

    /**
     * Write a file to the filesystem.
     */
    @objc func writeFile(_ call: CAPPluginCall) {
        let encoding = call.getString("encoding")
        let recursive = call.getBool("recursive") ?? false

        guard let file = call.getString("path") else {
            handleError(call, "path must be provided and must be a string.")
            return
        }

        guard let data = call.getString("data") else {
            handleError(call, "Data must be provided and must be a string.")
            return
        }

        let directory = call.getString("directory") ?? defaultDirectory

        guard let fileUrl = implementation.getFileUrl(at: file, in: directory) else {
            handleError(call, "Invalid path")
            return
        }

        do {
            let path = try implementation.writeFile(at: fileUrl, with: data, recursive: recursive, with: encoding)
            call.resolve([
                "uri": path
            ])
        } catch let error as Filesystem.FilesystemError {
            handleError(call, error.rawValue)
        } catch let error as NSError {
            handleError(call, error.localizedDescription, error)
        }
    }

    /**
     * Append to a file.
     */
    @objc func appendFile(_ call: CAPPluginCall) {
        let encoding = call.getString("encoding")

        guard let file = call.getString("path") else {
            handleError(call, "path must be provided and must be a string.")
            return
        }

        guard let data = call.getString("data") else {
            handleError(call, "Data must be provided and must be a string.")
            return
        }

        let directory = call.getString("directory") ?? defaultDirectory
        guard let fileUrl = implementation.getFileUrl(at: file, in: directory) else {
            handleError(call, "Invalid path")
            return
        }

        do {
            try implementation.appendFile(at: fileUrl, with: data, recursive: false, with: encoding)
        } catch let error as NSError {
            handleError(call, error.localizedDescription, error)
        }
    }

    /**
     * Delete a file.
     */
    @objc func deleteFile(_ call: CAPPluginCall) {
        guard let file = call.getString("path") else {
            handleError(call, "path must be provided and must be a string.")
            return
        }

        let directory = call.getString("directory") ?? defaultDirectory
        guard let fileUrl = implementation.getFileUrl(at: file, in: directory) else {
            handleError(call, "Invalid path")
            return
        }

        do {
            try implementation.deleteFile(at: fileUrl)
            call.resolve()
        } catch let error as NSError {
            handleError(call, error.localizedDescription, error)
        }
    }

    /**
     * Make a new directory, optionally creating parent folders first.
     */
    @objc func mkdir(_ call: CAPPluginCall) {
        guard let path = call.getString("path") else {
            handleError(call, "path must be provided and must be a string.")
            return
        }

        let recursive = call.getBool("recursive") ?? false
        let directory = call.getString("directory") ?? defaultDirectory
        guard let fileUrl = implementation.getFileUrl(at: path, in: directory) else {
            handleError(call, "Invalid path")
            return
        }

        do {
            try implementation.mkdir(at: fileUrl, recursive: recursive)
            call.resolve()
        } catch let error as NSError {
            handleError(call, error.localizedDescription, error)
        }
    }

    /**
     * Remove a directory.
     */
    @objc func rmdir(_ call: CAPPluginCall) {
        guard let path = call.get("path", String.self) else {
            handleError(call, "path must be provided and must be a string.")
            return
        }

        let directory = call.get("directory", String.self, defaultDirectory)!
        guard let fileUrl = implementation.getFileUrl(at: path, in: directory) else {
            handleError(call, "Invalid path")
            return
        }

        let recursive = call.getBool("recursive") ?? false

        do {
            try implementation.rmdir(at: fileUrl, recursive: recursive)
        } catch let error as Filesystem.FilesystemError {
            handleError(call, error.rawValue)
        } catch let error as NSError {
            handleError(call, error.localizedDescription, error)
        }
    }

    /**
     * Read the contents of a directory.
     */
    @objc func readdir(_ call: CAPPluginCall) {
        guard let path = call.get("path", String.self) else {
            handleError(call, "path must be provided and must be a string.")
            return
        }

        let directory = call.get("directory", String.self, defaultDirectory)!
        guard let fileUrl = implementation.getFileUrl(at: path, in: directory) else {
            handleError(call, "Invalid path")
            return
        }

        do {
            let directoryContents = try implementation.readdir(at: fileUrl)
            let directoryPathStrings = directoryContents.map {(url: URL) -> String in
                return url.lastPathComponent
            }
            call.resolve([
                "files": directoryPathStrings
            ])
        } catch {
            handleError(call, error.localizedDescription, error)
        }
    }

    @objc func stat(_ call: CAPPluginCall) {
        guard let path = call.getString("path") else {
            handleError(call, "path must be provided and must be a string.")
            return
        }

        let directory = call.get("directory", String.self, defaultDirectory)!
        guard let fileUrl = implementation.getFileUrl(at: path, in: directory) else {
            handleError(call, "Invalid path")
            return
        }

        do {
            let attr = try implementation.stat(at: fileUrl)
            call.resolve([
                "type": attr[.type] as? String ?? "",
                "size": attr[.size] as? UInt64 ?? "",
                "ctime": (attr[.creationDate] as? Date)?.timeIntervalSince1970 ?? "",
                "mtime": (attr[.modificationDate] as? Date)?.timeIntervalSince1970 ?? "",
                "uri": fileUrl.absoluteString
            ])
        } catch {
            handleError(call, error.localizedDescription, error)
        }
    }

    @objc func getUri(_ call: CAPPluginCall) {
        guard let path = call.get("path", String.self) else {
            handleError(call, "path must be provided and must be a string.")
            return
        }

        let directory = call.get("directory", String.self, defaultDirectory)!
        guard let fileUrl = implementation.getFileUrl(at: path, in: directory) else {
            handleError(call, "Invalid path")
            return
        }

        call.resolve([
            "uri": fileUrl.absoluteString
        ])

    }

    /**
     * Rename a file or directory.
     */
    @objc func rename(_ call: CAPPluginCall) {
        guard let from = call.get("from", String.self), let to = call.get("to", String.self) else {
            handleError(call, "Both to and from must be provided")
            return
        }

        let directory = call.getString("directory") ?? defaultDirectory
        let toDirectory = call.getString("toDirectory") ?? directory

        guard let fromUrl = implementation.getFileUrl(at: from, in: directory) else {
            handleError(call, "Invalid from path")
            return
        }

        guard let toUrl = implementation.getFileUrl(at: to, in: toDirectory) else {
            handleError(call, "Invalid to path")
            return
        }
        do {
            try implementation.rename(at: fromUrl, to: toUrl)
            call.resolve()
        } catch let error as NSError {
            handleError(call, error.localizedDescription, error)
        }
    }

    /**
     * Copy a file or directory.
     */
    @objc func copy(_ call: CAPPluginCall) {
        guard let from = call.getString("from"), let to = call.getString("to") else {
            handleError(call, "Both to and from must be provided")
            return
        }

        let directory = call.getString("directory") ?? defaultDirectory
        let toDirectory = call.getString("toDirectory") ?? directory

        guard let fromUrl = implementation.getFileUrl(at: from, in: directory) else {
            handleError(call, "Invalid from path")
            return
        }

        guard let toUrl = implementation.getFileUrl(at: to, in: toDirectory) else {
            handleError(call, "Invalid to path")
            return
        }
        do {
            try implementation.copy(at: fromUrl, to: toUrl)
            call.resolve()
        } catch let error as NSError {
            handleError(call, error.localizedDescription, error)
        }
    }

    /**
     * Helper for handling errors
     */
    private func handleError(_ call: CAPPluginCall, _ message: String, _ error: Error? = nil) {
        call.reject(message, nil, error)
    }

}
