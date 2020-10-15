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
    var call: CAPPluginCall

    init(call: CAPPluginCall, options: [String: Any]) {
        self.call = call

        super.init()

        // TODO: Allow user to configure accuracy, request/authorization mode
        self.locationManager.delegate = self
        self.locationManager.requestWhenInUseAuthorization()
        let shouldWatch = options["watch"] as? Bool ?? false
        if call.getBool("enableHighAccuracy", false) == true {
            if shouldWatch {
                self.locationManager.desiredAccuracy = kCLLocationAccuracyBestForNavigation
            } else {
                self.locationManager.desiredAccuracy = kCLLocationAccuracyBest
            }
        } else {
            self.locationManager.desiredAccuracy = kCLLocationAccuracyThreeKilometers
        }

        if shouldWatch {
            self.locationManager.startUpdatingLocation()
        } else {
            self.locationManager.requestLocation()
        }
    }

    public func stopUpdating() {
        self.locationManager.stopUpdatingLocation()
    }

    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        call.reject(error.localizedDescription)
    }

    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        if let location = locations.first {
            let result = makePosition(location)

            call.resolve(result)
        } else {
            // TODO: Handle case where location is nil
            call.resolve()
        }
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
