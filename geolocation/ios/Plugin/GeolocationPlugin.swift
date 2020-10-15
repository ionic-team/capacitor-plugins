import Foundation
import UIKit
import Capacitor

@objc(GeolocationPlugin)
public class GeolocationPlugin: CAPPlugin {
    var locationHandler: Geolocation?
    var watchLocationHandler: Geolocation?

    @objc func getCurrentPosition(_ call: CAPPluginCall) {
        DispatchQueue.main.async {
            self.locationHandler = Geolocation(call: call, options: [
                "watch": false
            ])
        }
    }

    @objc func watchPosition(_ call: CAPPluginCall) {
        call.save()

        DispatchQueue.main.async {
            self.watchLocationHandler = Geolocation(call: call, options: [
                "watch": true
            ])
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

            if self.watchLocationHandler != nil {
                self.watchLocationHandler?.stopUpdating()
            }
        }
        call.resolve()
    }

}
