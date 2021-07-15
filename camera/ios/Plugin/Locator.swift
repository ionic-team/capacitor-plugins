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

    var locationRequests: Array <Callback> = Array <Callback>()
    var headingRequests: Array <Callback> = Array <Callback>()

    var location: CLLocation? { return sharedLocationManager.location }
    var heading: CLHeading? { return sharedLocationManager.heading }
    var headingCnt: Int = 0
    var locationCnt: Int = 0
        
    lazy var sharedLocationManager: CLLocationManager = {
        let newLocationmanager = CLLocationManager()
        newLocationmanager.delegate = self
        newLocationmanager.desiredAccuracy = kCLLocationAccuracyBestForNavigation
        // ...
        print("sharedLocationManager!!")
        return newLocationmanager
    }()

    // MARK: - Authorization

    class func authorize() { shared.authorize() }
    func authorize() { sharedLocationManager.requestWhenInUseAuthorization()
        print("Trying to authorize!!")

    }

    // MARK: - Helpers

    func getLocation(callback: @escaping Callback) {
        if CLLocationManager.authorizationStatus() == .notDetermined {
            sharedLocationManager.requestWhenInUseAuthorization()
        } else {
            sharedLocationManager.startUpdatingLocation()
            self.locationRequests.append(callback)
            sharedLocationManager.startUpdatingLocation()
            print("Trying to get Location!!")
        }
    }
    
    func getHeading(callback: @escaping Callback) {
        if (CLLocationManager.headingAvailable()) {
            self.headingRequests.append(callback)
            sharedLocationManager.headingFilter = 1
            sharedLocationManager.startUpdatingHeading()
            print("Trying to get Heading!!")
        }
    }

    func resetLocation() {
        self.locationRequests = Array <Callback>()
        sharedLocationManager.stopUpdatingLocation()
    }
    
    func resetHeading() {
        self.headingRequests = Array <Callback>()
        sharedLocationManager.stopUpdatingHeading()
    }

    // MARK: - Delegate

    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        print("Location Error: \(String(describing: error))")
        for request in self.locationRequests { request(.Failure(error)) }
        self.resetLocation()
    }

    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        print("Location Result: \(String(describing: self.location))")
        for request in self.locationRequests { request(.Success(self))}

        if (locationCnt < 4) {
            locationCnt += 1
            return
        }
        locationCnt = 0
        
        self.resetLocation()
    }
   
    func  locationManager(_ manager: CLLocationManager, didUpdateHeading newHeading: CLHeading) {
        print("Heading Result: \(String(describing: heading))")
        for request in self.headingRequests { request(.Success(self))}

        if (headingCnt < 4) {
            headingCnt += 1
            return
        }
        headingCnt = 0
        
        self.resetHeading()
    }
}
