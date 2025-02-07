enum IONFileMethod: String {
    case read
    case writeFile
    case appendFile
    case deleteFile
    case mkdir
    case rmdir
    case readdir
    case stat
    case getUri
    case rename
    case copy
}

enum FilesystemError: Error {
    case bridgeNotInitialised
    case invalidDataEncodingCombination(method: IONFileMethod)
    case invalidPathParameter
    case invalidPath(_ path: String)
    case invalidDataParameter
    case bothPathsRequired
    case operationFailed(method: IONFileMethod, _ error: Error)

    func toCodeMessagePair() -> (code: String, message: String) {
        ("OS-PLUG-FILE-\(String(format: "%04d", code))", description)
    }
}

private extension FilesystemError {
    var code: Int {
        switch self {
        case .bridgeNotInitialised: 0
        case .invalidDataEncodingCombination: 0
        case .invalidPathParameter: 0
        case .invalidPath: 0
        case .invalidDataParameter: 0
        case .bothPathsRequired: 0
        case .operationFailed: 0
        }
    }

    var description: String {
        switch self {
        case .bridgeNotInitialised: "Capacitor bridge isn't initialized."
        case .invalidDataEncodingCombination(let method): "Can't decode the 'value' and 'encoding' combination for method '\(method.rawValue)'."
        case .invalidPathParameter: "'path' must be provided and must be a string."
        case .invalidPath(let path): "Invalid \(!path.isEmpty ? "'" + path + "' " : "")path."
        case .invalidDataParameter: "'data' must be provided and must be a string."
        case .bothPathsRequired: "Both 'to' and 'from' must be provided."
        case .operationFailed(let method, let error): "'\(method.rawValue) failed: \(error.localizedDescription)."
        }
    }
}
