// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorScreenReader",
    platforms: [.iOS(.v13)],
    products: [
        .library(
            name: "ScreenReaderPlugin",
            targets: ["ScreenReaderPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor6-spm-test.git", branch: "main")
    ],
    targets: [
        .target(
            name: "ScreenReaderPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor6-spm-test"),
                .product(name: "Cordova", package: "capacitor6-spm-test")
            ],
            path: "ios/Sources/ScreenReaderPlugin"),
        .testTarget(
            name: "ScreenReaderPluginTests",
            dependencies: ["ScreenReaderPlugin"],
            path: "ios/Tests/ScreenReaderPluginTests")
    ]
)