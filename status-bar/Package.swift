// swift-tools-version: 5.9

import PackageDescription

let package = Package(
    name: "CapacitorStatusBarPlugin",
    platforms: [.iOS(.v13)],
    products: [
        .library(
            name: "StatusBarPlugin",
            targets: ["StatusBarPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor6-spm-test.git", branch: "main")
    ],
    targets: [
        .target(
            name: "StatusBarPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor6-spm-test"),
                .product(name: "Cordova", package: "capacitor6-spm-test")],
            path: "ios/Sources/StatusBarPlugin"
        ),
        .testTarget(
            name: "StatusBarPluginTests",
            dependencies: ["StatusBarPlugin"],
            path: "ios/Tests/StatusBarPluginTests")
    ]
)
