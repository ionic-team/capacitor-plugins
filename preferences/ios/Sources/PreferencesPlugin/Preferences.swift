import Foundation

public struct PreferencesConfiguration {
    public enum Group {
        case named(String), cordovaNativeStorage
    }

    let suite: String
    let group: Group

    public init(for suite: String = "", for group: Group = .named("CapacitorStorage")) {
        self.suite = suite
        if !suite.isEmpty {
            self.group = .cordovaNativeStorage;
        } else {
            self.group = group
        }
    }
}

public class Preferences {
    private let configuration: PreferencesConfiguration

    private var defaults: UserDefaults {
        if !configuration.suite.isEmpty {
            if let i = UserDefaults.init(suiteName: configuration.suite) {
                return i
            }
        }

        return UserDefaults.standard
    }

    private var prefix: String {
        if !configuration.suite.isEmpty {
            return ""
        }

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
