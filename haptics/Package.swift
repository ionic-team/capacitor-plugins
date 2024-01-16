// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorHapticsPlugin",
    platforms: [.iOS(.v13)],
    products: [
        .library(
            name: "HapticsPlugin",
            targets: ["HapticsPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor6-spm-test.git", branch: "main")
    ],
    targets: [
        .target(
            name: "HapticsPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor6-spm-test"),
                .product(name: "Cordova", package: "capacitor6-spm-test")
            ],
            path: "ios/Sources/HapticsPlugin"),
        .testTarget(
            name: "HapticsPluginTests",
            dependencies: ["HapticsPlugin"],
            path: "ios/Tests/HapticsPluginTests")
    ]
)
