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
        .package(url: "https://github.com/ionic-team/capacitor-spm.git", branch: "main")
    ],
    targets: [
        .target(
            name: "SharePlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-spm"),
                .product(name: "Cordova", package: "capacitor-spm")
            ],
            path: "ios/Sources/SharePlugin"),
        .testTarget(
            name: "SharePluginTests",
            dependencies: ["SharePlugin"],
            path: "ios/Tests/SharePluginTests")
    ]
)
