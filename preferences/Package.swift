// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorPreferences",
    platforms: [.iOS(.v14)],
    products: [
        .library(
            name: "CapacitorPreferences",
            targets: ["PreferencesPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", from: "7.0.0")
    ],
    targets: [
        .target(
            name: "PreferencesPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/PreferencesPlugin"),
        .testTarget(
            name: "PreferencesPluginTests",
            dependencies: ["PreferencesPlugin"],
            path: "ios/Tests/PreferencesPluginTests")
    ]
)
