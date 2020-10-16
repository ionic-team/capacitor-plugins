import Foundation
import UIKit
import Capacitor

@objc(GeolocationPlugin)
public class GeolocationPlugin: CAPPlugin {

    private let implementation = Geolocation()

    @objc func getCurrentPosition(_ call: CAPPluginCall) {
        DispatchQueue.main.async {
            self.implementation.getLocation(call: call)
        }
    }

    @objc func watchPosition(_ call: CAPPluginCall) {
        call.save()

        DispatchQueue.main.async {
            self.implementation.watchLocation(call: call)
        }
    }

    @objc func clearWatch(_ call: CAPPluginCall) {
        guard let callbackId = call.getString("id") else {
            CAPLog.print("Must supply id")
            return
        }
        let savedCall = bridge?.getSavedCall(callbackId)
        if savedCall != nil {
            bridge?.releaseCall(savedCall!)

            self.implementation.stopUpdating()
        }
        call.resolve()
    }

    @objc func checkPermissions(_ call: CAPPluginCall) {
        let result = self.implementation.checkPermissions()
        call.resolve(result)
    }

    @objc func requestPermissions(_ call: CAPPluginCall) {

    }

}
