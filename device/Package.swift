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
        .package(url: "https://github.com/ionic-team/capacitor.git", from: "9.0.0-alpha.7")
    ],
    targets: [
        .target(
            name: "DevicePlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor")
            ],
            path: "ios/Sources/DevicePlugin"),
        .testTarget(
            name: "DevicePluginTests",
            dependencies: ["DevicePlugin"],
            path: "ios/Tests/DevicePluginTests")
    ]
)
