// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorDevice",
    platforms: [.iOS(.v13)],
    products: [
        .library(
            name: "DevicePlugin",
            targets: ["DevicePlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor6-spm-test.git", branch: "main")
    ],
    targets: [
        .target(
            name: "DevicePlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor6-spm-test"),
                .product(name: "Cordova", package: "capacitor6-spm-test")
            ],
            path: "ios/Sources/DevicePlugin"),
        .testTarget(
            name: "DevicePluginTests",
            dependencies: ["DevicePlugin"],
            path: "ios/Tests/DevicePluginTests")
    ]
)
