import Foundation

public struct PreferencesConfiguration {
    public enum Group {
        case named(String), cordovaNativeStorage
    }

    let group: Group
    let suiteName: String?

    public init(for group: Group = .named("CapacitorStorage"), suiteName: String? = nil) {
        self.group = group
        self.suiteName = suiteName
    }
}

public class Preferences {
    private let configuration: PreferencesConfiguration

    private var defaults: UserDefaults {
        return UserDefaults(suiteName: suiteName)!
    }

    private var prefix: String {
        switch configuration.group {
        case .cordovaNativeStorage:
            return ""
        case let .named(group):
            return group + "."
        }
    }

    private var rawKeys: [String] {
        return defaults.dictionaryRepresentation().keys.filter { $0.hasPrefix(prefix) }
    }

    public init(with configuration: PreferencesConfiguration) {
        self.configuration = configuration
    }

    public func get(by key: String) -> String? {
        return defaults.string(forKey: applyPrefix(to: key))
    }

    public func set(_ value: String, for key: String) {
        defaults.set(value, forKey: applyPrefix(to: key))
    }

    public func remove(by key: String) {
        defaults.removeObject(forKey: applyPrefix(to: key))
    }

    public func removeAll() {
        for key in rawKeys {
            defaults.removeObject(forKey: key)
        }
    }

    public func keys() -> [String] {
        return rawKeys.map { String($0.dropFirst(prefix.count)) }
    }

    private func applyPrefix(to key: String) -> String {
        return prefix + key
    }
}
