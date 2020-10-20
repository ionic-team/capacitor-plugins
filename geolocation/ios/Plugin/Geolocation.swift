import Foundation
import CoreLocation
import Capacitor

public struct GeolocationCoords {
    public var latitude: Double
    public var longitude: Double
    public init(latitude: Double, longitude: Double) {
        self.latitude = latitude
        self.longitude = longitude
    }
}

class Geolocation: NSObject, CLLocationManagerDelegate {
    var locationManager = CLLocationManager()
    var call: CAPPluginCall?

    func getLocation(call: CAPPluginCall) {
        self.call = call

        self.locationManager.delegate = self
        self.locationManager.requestWhenInUseAuthorization()

        if call.getBool("enableHighAccuracy", false) == true {
            self.locationManager.desiredAccuracy = kCLLocationAccuracyBest
        } else {
            self.locationManager.desiredAccuracy = kCLLocationAccuracyThreeKilometers
        }

        self.locationManager.requestLocation()
    }

    func watchLocation(call: CAPPluginCall) {
        self.call = call

        self.locationManager.delegate = self
        self.locationManager.requestWhenInUseAuthorization()

        if call.getBool("enableHighAccuracy", false) == true {
            self.locationManager.desiredAccuracy = kCLLocationAccuracyBestForNavigation
        } else {
            self.locationManager.desiredAccuracy = kCLLocationAccuracyThreeKilometers
        }

        self.locationManager.startUpdatingLocation()
    }

    public func stopUpdating() {
        self.locationManager.stopUpdatingLocation()
    }

    public func checkPermissions() -> [String: Any] {
        return [
            "location": "granted"
        ]
    }

    public func requestPermissions() {
        self.locationManager.delegate = self
        self.locationManager.requestWhenInUseAuthorization()
    }

    func makePosition(_ location: CLLocation) -> JSObject {
        var ret = JSObject()
        var coords = JSObject()
        coords["latitude"] = location.coordinate.latitude
        coords["longitude"] = location.coordinate.longitude
        coords["accuracy"] = location.horizontalAccuracy
        coords["altitude"] = location.altitude
        coords["altitudeAccuracy"] = location.verticalAccuracy
        coords["speed"] = location.speed
        coords["heading"] = location.course
        ret["timestamp"] = NSNumber(value: Int((location.timestamp.timeIntervalSince1970 * 1000)))
        ret["coords"] = coords
        return ret
    }
}
