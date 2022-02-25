import Foundation
import Capacitor

public struct GoogleMapPadding {
    let top: Float
    let bottom: Float
    let left: Float
    let right: Float

    init(fromJSObject: JSObject) throws {
        top = fromJSObject["top"] as? Float ?? 0
        bottom = fromJSObject["bottom"] as? Float ?? 0
        left = fromJSObject["left"] as? Float ?? 0
        right = fromJSObject["right"] as? Float ?? 0
    }
}
