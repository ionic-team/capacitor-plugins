// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorScreenReader",
    platforms: [.iOS(.v16)],
    products: [
        .library(
            name: "CapacitorScreenReader",
            targets: ["ScreenReaderPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", from: "9.0.0-alpha.5")
    ],
    targets: [
        .target(
            name: "ScreenReaderPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/ScreenReaderPlugin"),
        .testTarget(
            name: "ScreenReaderPluginTests",
            dependencies: ["ScreenReaderPlugin"],
            path: "ios/Tests/ScreenReaderPluginTests")
    ]
)
