import Foundation

public enum GoogleMapErrors: Error {
    case invalidMapId
    case mapNotFound
    case invalidArguments(_ description: String)
    case unhandledError(_ description: String)
}

public struct GoogleMapErrorObject {
    let extra: [String: Any]?
    let code: Int
    let message: String
    init(_ code: Int, _ message: String, _ extra: [String: Any]? = nil) {
        self.code = code
        self.message = message
        self.extra = extra
    }

    var asDictionary: [String: Any] {
        return ["code": code, "message": message, "extra": extra ?? []]
    }
}

public func getErrorObject(_ error: Error) -> GoogleMapErrorObject {
    switch error {
    case GoogleMapErrors.invalidMapId:
        return GoogleMapErrorObject(1, "Missing or invalid map id.")
    case GoogleMapErrors.mapNotFound:
        return GoogleMapErrorObject(2, "Map not found for provided id.")
    case GoogleMapErrors.invalidArguments(let msg):
        return GoogleMapErrorObject(3, "Invalid Arguments Provided: \(msg)")
    case GoogleMapErrors.unhandledError(let msg):
        return GoogleMapErrorObject(0, "Unhandled Error: \(msg)")
    default:
        return GoogleMapErrorObject(0, "Unhandled Error: \(error.localizedDescription)")
    }
}
