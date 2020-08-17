import Foundation

public struct StorageConfiguration {
    enum Group {
        case named(String), cordovaNativeStorage
    }

    let group: Group

    init(for group: Group = .named("CapacitorStorage")) {
        self.group = group
    }
}

@objc public class Storage: NSObject {
    private let configuration: StorageConfiguration

    private var defaults: UserDefaults {
        return UserDefaults.standard
    }

    private var prefix: String {
        switch configuration.group {
        case .cordovaNativeStorage:
            return ""
        case let .named(group):
            return group + "."
        }
    }

    private var keys: [String] {
        return defaults.dictionaryRepresentation().keys.filter { $0.hasPrefix(prefix) }
    }

    public init(with configuration: StorageConfiguration) {
        self.configuration = configuration
    }

    @objc func get(by key: String) -> String? {
        return defaults.string(forKey: applyPrefix(to: key))
    }

    @objc func set(_ value: String, for key: String) {
        defaults.set(value, forKey: applyPrefix(to: key))
    }

    @objc func remove(by key: String) {
        defaults.removeObject(forKey: applyPrefix(to: key))
    }

    @objc func removeAll() {
        for key in keys {
            defaults.removeObject(forKey: key)
        }
    }

    @objc func getKeys() -> [String] {
        return keys.map { String($0.dropFirst(prefix.count)) }
    }

    private func applyPrefix(to key: String) -> String {
        return prefix + key
    }
}
