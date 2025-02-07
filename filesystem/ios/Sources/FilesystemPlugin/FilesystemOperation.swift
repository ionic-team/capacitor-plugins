import Foundation
import IONFilesystemLib

enum FilesystemOperation {
    // Read Operations
    case read(url: URL, encoding: IONFILEEncoding)
    case readdir(url: URL)
    case stat(url: URL)
    case getUri(url: URL)

    // Write Operations
    case write(url: URL, encodingMapper: IONFILEEncodingValueMapper, recursive: Bool)
    case append(url: URL, encodingMapper: IONFILEEncodingValueMapper, recursive: Bool)

    // Directory Operations
    case mkdir(url: URL, recursive: Bool)
    case rmdir(url: URL, recursive: Bool)

    // File Management Operations
    case delete(url: URL)
    case rename(source: URL, destination: URL)
    case copy(source: URL, destination: URL)
}
