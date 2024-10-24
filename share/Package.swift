// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorShare",
    platforms: [.iOS(.v13)],
    products: [
        .library(
            name: "CapacitorShare",
            targets: ["SharePlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", from: "6.0.0")
    ],
    targets: [
        .target(
            name: "SharePlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/SharePlugin"),
        .testTarget(
            name: "SharePluginTests",
            dependencies: ["SharePlugin"],
            path: "ios/Tests/SharePluginTests")
    ]
)
