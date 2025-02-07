import Capacitor
import Foundation
import IONFilesystemLib

struct FilesystemLocationResolver {
    let service: FileService

    func resolveSinglePath(from call: CAPPluginCall) -> Result<URL, FilesystemError> {
        guard let path = call.getString(Constants.MethodParameter.path) else {
            return .failure(.invalidPathParameter)
        }

        let directory = call.getSearchPath(Constants.MethodParameter.directory)
        return resolveURL(path: path, directory: directory)
    }

    func resolveDualPaths(from call: CAPPluginCall) -> Result<(source: URL, destination: URL), FilesystemError> {
        guard let fromPath = call.getString(Constants.MethodParameter.from), let toPath = call.getString(Constants.MethodParameter.to) else {
            return .failure(.bothPathsRequired)
        }

        let fromDirectory = call.getSearchPath(Constants.MethodParameter.directory)
        let toDirectory = call.getSearchPath(Constants.MethodParameter.toDirectory, withDefault: fromDirectory)

        return resolveURL(path: fromPath, directory: fromDirectory)
            .flatMap { sourceURL in
                resolveURL(path: toPath, directory: toDirectory)
                    .map { (source: sourceURL, destination: $0) }
            }
    }

    private func resolveURL(path: String, directory: IONFILESearchPath) -> Result<URL, FilesystemError> {
        return if let url = try? service.getFileURL(atPath: path, withSearchPath: directory) {
            .success(url)
        } else {
            .failure(.invalidPath(path))
        }
    }
}
