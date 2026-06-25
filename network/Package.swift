// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorNetwork",
    platforms: [.iOS(.v16)],
    products: [
        .library(
            name: "CapacitorNetwork",
            targets: ["CAPNetworkPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", from: "9.0.0-alpha.5")
    ],
    targets: [
        .target(
            name: "CAPNetworkPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/NetworkPlugin"),
        .testTarget(
            name: "CAPNetworkPluginTests",
            dependencies: ["CAPNetworkPlugin"],
            path: "ios/Tests/NetworkPluginTests")
    ]
)
