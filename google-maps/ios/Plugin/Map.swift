import Foundation
import GoogleMaps

public struct LatLng: Codable {
    let lat: Float
    let lng: Float
}

public class Map {
    init(config: GoogleMapConfig, mapViewController: GMViewController) {}
}
