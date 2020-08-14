import Foundation

public struct StorageConfiguration {
    var group = "CapacitorStorage"
}

@objc public class Storage: NSObject {
    private var configuration: StorageConfiguration

    public init(withConfiguration configuration: StorageConfiguration) {
        self.configuration = configuration
    }

    @objc func get(byKey key: String) -> String? {
        return defaults.string(forKey: applyPrefix(toKey: key))
    }

    @objc func set(_ value: String, forKey key: String) {
        defaults.set(value, forKey: applyPrefix(toKey: key))
    }

    @objc func remove(byKey key: String) {
        defaults.removeObject(forKey: applyPrefix(toKey: key))
    }

    @objc func removeAll() {
        for key in keys {
            defaults.removeObject(forKey: key)
        }
    }

    @objc func getKeys() -> [String] {
        return keys.map { String($0.dropFirst(prefix.count)) }
    }

    private var prefix: String {
        return configuration.group == "NativeStorage" ? "" : configuration.group + "."
    }

    private var defaults: UserDefaults {
        return UserDefaults.standard
    }

    private var keys: [String] {
        return defaults.dictionaryRepresentation().keys.filter { $0.hasPrefix(prefix) }
    }

    private func applyPrefix(toKey key: String) -> String {
        return prefix + key
    }
}
