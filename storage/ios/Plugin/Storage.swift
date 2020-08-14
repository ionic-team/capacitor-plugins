import Foundation

@objc public class Storage: NSObject {
    var prefix = "_cap_"

    @objc func get(byKey key: String) -> String? {
        return defaults.string(forKey: applyPrefix(onKey: key))
    }

    @objc func set(_ value: String, forKey key: String) {
        defaults.set(value, forKey: applyPrefix(onKey: key))
    }

    @objc func remove(byKey key: String) {
        defaults.removeObject(forKey: key)
    }

    @objc func removeAll() {
        for key in keys {
            defaults.removeObject(forKey: key)
        }
    }

    @objc func getKeys() -> [String] {
        keys.map { String($0.dropFirst(prefix.count)) }
    }

    private var defaults: UserDefaults {
        return UserDefaults.standard
    }

    private var keys: [String] {
        return defaults.dictionaryRepresentation().keys.filter { $0.hasPrefix(prefix) }
    }

    private func applyPrefix(onKey key: String) -> String {
        return prefix + key
    }
}
