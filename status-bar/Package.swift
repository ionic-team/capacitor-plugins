// swift-tools-version: 5.9

import PackageDescription

let package = Package(
    name: "CapacitorStatusBar",
    platforms: [.iOS(.v14)],
    products: [
        .library(
            name: "CapacitorStatusBar",
            targets: ["StatusBarPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", from: "7.0.0")
    ],
    targets: [
        .target(
            name: "StatusBarPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")],
            path: "ios/Sources/StatusBarPlugin"
        ),
        .testTarget(
            name: "StatusBarPluginTests",
            dependencies: ["StatusBarPlugin"],
            path: "ios/Tests/StatusBarPluginTests")
    ]
)
