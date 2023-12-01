// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorGeolocation",
    platforms: [.iOS(.v13)],
    products: [
        .library(
            name: "GeolocationPlugin",
            targets: ["GeolocationPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor6-spm-test.git", branch: "main")
    ],
    targets: [
        .target(
            name: "GeolocationPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor6-spm-test"),
                .product(name: "Cordova", package: "capacitor6-spm-test")
            ],
            path: "ios/Sources/GeolocationPlugin"),
        .testTarget(
            name: "GeolocationPluginTests",
            dependencies: ["GeolocationPlugin"],
            path: "ios/Tests/GeolocationPluginTests")
    ]
)
