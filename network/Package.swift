// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorNetwork",
    platforms: [.iOS(.v13)],
    products: [
        .library(
            name: "CAPNetworkPlugin",
            targets: ["CAPNetworkPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-spm.git", branch: "main")
    ],
    targets: [
        .target(
            name: "CAPNetworkPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-spm"),
                .product(name: "Cordova", package: "capacitor-spm")
            ],
            path: "ios/Sources/NetworkPlugin"),
        .testTarget(
            name: "CAPNetworkPluginTests",
            dependencies: ["CAPNetworkPlugin"],
            path: "ios/Tests/NetworkPluginTests")
    ]
)
