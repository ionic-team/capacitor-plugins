import CoreLocation

// https://stackoverflow.com/a/32832351
// https://github.com/heckj/swiftui-notes/blob/master/SwiftUI-Notes/LocationModelProxy.swift
// https://www.advancedswift.com/user-location-in-swift/
class Locator: NSObject, CLLocationManagerDelegate {
    enum Result <T> {
      case Success(T)
      case Failure(Error)
    }

    static let shared: Locator = Locator()

    typealias Callback = (Result <Locator>) -> Void

    var requests: Array <Callback> = Array <Callback>()

    var location: CLLocation? { return sharedLocationManager.location  }

    lazy var sharedLocationManager: CLLocationManager = {
        let newLocationmanager = CLLocationManager()
        newLocationmanager.delegate = self
        newLocationmanager.desiredAccuracy = kCLLocationAccuracyBestForNavigation
        // ...
        print("sharedLocationManager!!")
        
        if CLLocationManager.authorizationStatus() == .notDetermined {
            newLocationmanager.requestWhenInUseAuthorization()
        } else {
            newLocationmanager.startUpdatingLocation()
        }
        
        return newLocationmanager
    }()

    // MARK: - Authorization

    class func authorize() { shared.authorize() }
    func authorize() { sharedLocationManager.requestWhenInUseAuthorization()
        print("Trying to authorize!!")

    }

    // MARK: - Helpers

    func locate(callback: @escaping Callback) {
        self.requests.append(callback)
        sharedLocationManager.startUpdatingLocation()
        sharedLocationManager.startUpdatingHeading()
        print("Trying to Locate!!")
    }

    func reset() {
        self.requests = Array <Callback>()
        sharedLocationManager.stopUpdatingLocation()
    }

    // MARK: - Delegate

    func locationManager(_ manager: CLLocationManager, didFailWithError error: NSError) {
        print("DID NOT UPDATE LOCATION!!")
        print("Location Result: \(String(describing: error))")
        for request in self.requests { request(.Failure(error)) }
        self.reset()
    }

    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        print("DID UPDATE LOCATION!!")
        print("Location Result: \(String(describing: self.location))")
        for request in self.requests { request(.Success(self)) }
        self.reset()
    }

    func headingManger(_ manager: CLLocationManager, didUpdateHeading: [CLHeading]) {
        var heading = manager.heading?.magneticHeading
        print("DID UPDATE HEADING!!")
        print("heading = \(heading)")
    }
}
