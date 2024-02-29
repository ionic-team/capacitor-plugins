// swift-tools-version: 5.9

import PackageDescription

let package = Package(
    name: "CapacitorStatusBar",
    platforms: [.iOS(.v13)],
    products: [
        .library(
            name: "CapacitorStatusBar",
            targets: ["StatusBarPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-spm.git", branch: "main")
    ],
    targets: [
        .target(
            name: "StatusBarPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-spm"),
                .product(name: "Cordova", package: "capacitor-spm")],
            path: "ios/Sources/StatusBarPlugin"
        ),
        .testTarget(
            name: "StatusBarPluginTests",
            dependencies: ["StatusBarPlugin"],
            path: "ios/Tests/StatusBarPluginTests")
    ]
)
