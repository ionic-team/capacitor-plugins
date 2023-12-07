// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorScreenOrientation",
    platforms: [.iOS(.v13)],
    products: [
        .library(
            name: "ScreenOrientationPlugin",
            targets: ["ScreenOrientationPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor6-spm-test.git", branch: "main")
    ],
    targets: [
        .target(
            name: "ScreenOrientationPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor6-spm-test"),
                .product(name: "Cordova", package: "capacitor6-spm-test")
            ],
            path: "ios/Sources/ScreenOrientationPlugin"),
        .testTarget(
            name: "ScreenOrientationPluginTests",
            dependencies: ["ScreenOrientationPlugin"],
            path: "ios/Tests/ScreenOrientationPluginTests")
    ]
)