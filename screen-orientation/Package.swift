// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorScreenOrientation",
    platforms: [.iOS(.v16)],
    products: [
        .library(
            name: "CapacitorScreenOrientation",
            targets: ["ScreenOrientationPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", from: "9.0.0-alpha.5")
    ],
    targets: [
        .target(
            name: "ScreenOrientationPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/ScreenOrientationPlugin"),
        .testTarget(
            name: "ScreenOrientationPluginTests",
            dependencies: ["ScreenOrientationPlugin"],
            path: "ios/Tests/ScreenOrientationPluginTests")
    ]
)
