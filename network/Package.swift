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
        .package(url: "https://github.com/ionic-team/capacitor.git", from: "9.0.0-alpha.7")
    ],
    targets: [
        .target(
            name: "CAPNetworkPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor")
            ],
            path: "ios/Sources/NetworkPlugin"),
        .testTarget(
            name: "CAPNetworkPluginTests",
            dependencies: ["CAPNetworkPlugin"],
            path: "ios/Tests/NetworkPluginTests")
    ]
)
