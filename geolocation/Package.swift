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
        .package(url: "https://github.com/ionic-team/capacitor-spm.git", branch: "main")
    ],
    targets: [
        .target(
            name: "GeolocationPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-spm"),
                .product(name: "Cordova", package: "capacitor-spm")
            ],
            path: "ios/Sources/GeolocationPlugin"),
        .testTarget(
            name: "GeolocationPluginTests",
            dependencies: ["GeolocationPlugin"],
            path: "ios/Tests/GeolocationPluginTests")
    ]
)
