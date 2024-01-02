// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorShare",
    platforms: [.iOS(.v13)],
    products: [
        .library(
            name: "SharePlugin",
            targets: ["SharePlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor6-spm-test.git", branch: "main")
    ],
    targets: [
        .target(
            name: "SharePlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor6-spm-test"),
                .product(name: "Cordova", package: "capacitor6-spm-test")
            ],
            path: "ios/Sources/SharePlugin"),
        .testTarget(
            name: "SharePluginTests",
            dependencies: ["SharePlugin"],
            path: "ios/Tests/SharePluginTests")
    ]
)