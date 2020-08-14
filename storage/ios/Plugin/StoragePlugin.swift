import Foundation
import Capacitor

@objc(StoragePlugin)
public class StoragePlugin: CAPPlugin {
    private let storage = Storage()

    @objc func get(_ call: CAPPluginCall) {
        guard let key = call.getString("key") else {
            call.reject("Must provide a key")
            return
        }

        let value = storage.get(byKey: key)

        call.resolve([
            "value": value as Any
        ])
    }

    @objc func set(_ call: CAPPluginCall) {
        guard let key = call.getString("key") else {
            call.reject("Must provide a key")
            return
        }
        let value = call.getString("value", "") ?? ""

        storage.set(value, forKey: key)
        call.resolve()
    }

    @objc func remove(_ call: CAPPluginCall) {
        guard let key = call.getString("key") else {
            call.reject("Must provide a key")
            return
        }

        storage.remove(byKey: key)
        call.resolve()
    }

    @objc func keys(_ call: CAPPluginCall) {
        let keys = storage.getKeys()

        call.resolve([
            "keys": keys
        ])
    }

    @objc func clear(_ call: CAPPluginCall) {
        storage.removeAll()
        call.resolve()
    }
}
