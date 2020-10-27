import Foundation
import CoreLocation
import UIKit
import Capacitor

@objc(GeolocationPlugin)
public class GeolocationPlugin: CAPPlugin, CLLocationManagerDelegate {

    enum CallType {
        case permissions
        case singleUpdate
        case watch
    }

    var locationManager = CLLocationManager()
    private var callQueue: [String: CallType] = [:]

    @objc func getCurrentPosition(_ call: CAPPluginCall) {
        call.save()
        callQueue[call.callbackId] = .singleUpdate

        DispatchQueue.main.async {
            self.locationManager.delegate = self
            self.locationManager.requestWhenInUseAuthorization()

            if call.getBool("enableHighAccuracy", false) == true {
                self.locationManager.desiredAccuracy = kCLLocationAccuracyBest
            } else {
                self.locationManager.desiredAccuracy = kCLLocationAccuracyThreeKilometers
            }

            self.locationManager.requestLocation()
        }
    }

    @objc func watchPosition(_ call: CAPPluginCall) {
        call.save()
        callQueue[call.callbackId] = .watch

        DispatchQueue.main.async {
            self.locationManager.delegate = self
            self.locationManager.requestWhenInUseAuthorization()

            if call.getBool("enableHighAccuracy", false) == true {
                self.locationManager.desiredAccuracy = kCLLocationAccuracyBestForNavigation
            } else {
                self.locationManager.desiredAccuracy = kCLLocationAccuracyThreeKilometers
            }

            self.locationManager.startUpdatingLocation()
        }
    }

    @objc func clearWatch(_ call: CAPPluginCall) {
        guard let callbackId = call.getString("id") else {
            CAPLog.print("Must supply id")
            return
        }
        if let savedCall = bridge?.getSavedCall(callbackId) {
            bridge?.releaseCall(savedCall)

            self.stopUpdating()
        }
        call.resolve()
    }

    public func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        let removalQueue = callQueue.filter { $0.value == .permissions || $0.value == .singleUpdate }
        callQueue = callQueue.filter { $0.value == .watch }

        for (id, _) in removalQueue {
            if let call = bridge?.getSavedCall(id) {
                call.reject(error.localizedDescription)
                bridge?.releaseCall(call)
            }
        }

        for (id, _) in callQueue {
            if let call = bridge?.getSavedCall(id) {
                call.reject(error.localizedDescription)
            }
        }
    }

    public func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        let removalQueue = callQueue.filter { $0.value == .singleUpdate }
        callQueue = callQueue.filter { $0.value == .watch }

        for (id, _) in removalQueue {
            if let call = bridge?.getSavedCall(id) {
                reportLocation(call, locations)
                bridge?.releaseCall(call)
            }
        }

        for (id, _) in callQueue {
            if let call = bridge?.getSavedCall(id) {
                reportLocation(call, locations)
            }
        }
    }

    public func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
        let removalQueue = callQueue.filter { $0.value == .permissions }
        callQueue = callQueue.filter { $0.value != .permissions }

        for (id, _) in removalQueue {
            if let call = bridge?.getSavedCall(id) {
                checkPermissions(call)
                bridge?.releaseCall(call)
            }
        }
    }

    public func stopUpdating() {
        self.locationManager.stopUpdatingLocation()
    }

    private func reportLocation(_ call: CAPPluginCall, _ locations: [CLLocation]) {
        if let location = locations.first {
            let result = makePosition(location)
            call.resolve(result)
        } else {
            // TODO: Handle case where location is nil
            call.resolve()
        }
    }

    @objc func checkPermissions(_ call: CAPPluginCall) {
        var status: String = ""

        if CLLocationManager.locationServicesEnabled() {
            switch CLLocationManager.authorizationStatus() {
            case .notDetermined:
                status = "prompt"
            case .restricted, .denied:
                status = "denied"
            case .authorizedAlways, .authorizedWhenInUse:
                status = "granted"
            @unknown default:
                status = "prompt"
            }
        } else {
            call.reject("Location services are not enabled")
            return
        }

        let result = [
            "location": status
        ]

        call.resolve(result)
    }

    @objc func requestPermissions(_ call: CAPPluginCall) {
        if CLLocationManager.locationServicesEnabled() {
            // If state is not yet determined, request perms.
            // Otherwise, report back the state right away
            if CLLocationManager.authorizationStatus() == .notDetermined {
                call.save()
                callQueue[call.callbackId] = .permissions

                DispatchQueue.main.async {
                    self.locationManager.delegate = self
                    self.locationManager.requestWhenInUseAuthorization()
                }
            } else {
                checkPermissions(call)
            }
        } else {
            call.reject("Location services are not enabled")
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
