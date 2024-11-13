// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorHaptics",
    platforms: [.iOS(.v14)],
    products: [
        .library(
            name: "CapacitorHaptics",
            targets: ["HapticsPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", branch: "main")
    ],
    targets: [
        .target(
            name: "HapticsPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/HapticsPlugin"),
        .testTarget(
            name: "HapticsPluginTests",
            dependencies: ["HapticsPlugin"],
            path: "ios/Tests/HapticsPluginTests")
    ]
)
