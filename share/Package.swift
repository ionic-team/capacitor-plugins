// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorShare",
    platforms: [.iOS(.v16)],
    products: [
        .library(
            name: "CapacitorShare",
            targets: ["SharePlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor.git", from: "9.0.0-alpha.7")
    ],
    targets: [
        .target(
            name: "SharePlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor")
            ],
            path: "ios/Sources/SharePlugin"),
        .testTarget(
            name: "SharePluginTests",
            dependencies: ["SharePlugin"],
            path: "ios/Tests/SharePluginTests")
    ]
)
