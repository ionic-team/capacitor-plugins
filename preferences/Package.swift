// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorPreferences",
    platforms: [.iOS(.v13)],
    products: [
        .library(
            name: "PreferencesPlugin",
            targets: ["PreferencesPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-spm.git", branch: "main")
    ],
    targets: [
        .target(
            name: "PreferencesPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-spm"),
                .product(name: "Cordova", package: "capacitor-spm")
            ],
            path: "ios/Sources/PreferencesPlugin"),
        .testTarget(
            name: "PreferencesPluginTests",
            dependencies: ["PreferencesPlugin"],
            path: "ios/Tests/PreferencesPluginTests")
    ]
)
