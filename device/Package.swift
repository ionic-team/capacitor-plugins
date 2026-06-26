// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorDevice",
    platforms: [.iOS(.v16)],
    products: [
        .library(
            name: "CapacitorDevice",
            targets: ["DevicePlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", from: "9.0.0-alpha.5")
    ],
    targets: [
        .target(
            name: "DevicePlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/DevicePlugin"),
        .testTarget(
            name: "DevicePluginTests",
            dependencies: ["DevicePlugin"],
            path: "ios/Tests/DevicePluginTests")
    ]
)
