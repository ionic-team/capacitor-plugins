import Foundation
import Capacitor

@objc(CapacitorGoogleMapsPlugin)
public class CapacitorGoogleMapsPlugin: CAPPlugin {
    private var maps = Dictionary<String, Map>()
    
    @objc func create(_ call: CAPPluginCall) {
        guard let id = call.getString("id") else {
            call.reject("map id cannot be empty")
            return
        }
        
        let newMap = Map()
        self.maps[id] = newMap
        
        call.resolve()
    }
    
    @objc func destroy(_ call: CAPPluginCall) {
        guard let id = call.getString("id") else {
            call.reject("map id cannot be empty")
            return
        }
        
        let removedMap = self.maps.removeValue(forKey: id)
        if removedMap == nil {
            call.reject("map for key not found")
            return
        }
        
        call.resolve()
    }
}
