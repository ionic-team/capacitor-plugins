import IONFilesystemLib

extension IONFILEStringEncoding {
    static func create(from text: String) -> Self {
        switch text {
        case Constants.StringEncodingValue.ascii: .ascii
        case Constants.StringEncodingValue.utf16: .utf16
        case Constants.StringEncodingValue.utf8: .utf8
        default: .utf8
        }
    }
}

extension IONFILEDirectoryType {
    static func create(from text: String) -> Self? {
        switch text {
        case Constants.DirectoryTypeValue.cache: .cache
        case Constants.DirectoryTypeValue.data, Constants.DirectoryTypeValue.documents, Constants.DirectoryTypeValue.external, Constants.DirectoryTypeValue.externalCache, Constants.DirectoryTypeValue.externalStorage: .document
        case Constants.DirectoryTypeValue.library: .library
        default: nil
        }
    }
}

extension IONFILEEncodingValueMapper: @retroactive CustomStringConvertible {
    public var description: String {
        switch self {
        case .byteBuffer(let data): data.base64EncodedString()
        case .string(_, let text): text
        @unknown default: ""
        }
    }
}

extension IONFILEItemAttributeModel {
    typealias JSResult = [String: Any]
    func toJSResult(with url: URL) -> JSResult {
        [
            Constants.ItemAttributeJSONKey.name: url.lastPathComponent,
            Constants.ItemAttributeJSONKey.type: type.description,
            Constants.ItemAttributeJSONKey.size: size,
            Constants.ItemAttributeJSONKey.ctime: UInt64(creationDateTimestamp.rounded()),
            Constants.ItemAttributeJSONKey.mtime: UInt64(modificationDateTimestamp.rounded()),
            Constants.ItemAttributeJSONKey.uri: url.absoluteString
        ]
    }
}

extension IONFILEItemType {
    var description: String {
        switch self {
        case .directory: Constants.FileItemTypeValue.directory
        case .file: Constants.FileItemTypeValue.file
        @unknown default: Constants.FileItemTypeValue.fallback
        }
    }
}
