// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorScreenReader",
    platforms: [.iOS(.v13)],
    products: [
        .library(
            name: "CapacitorScreenReader",
            targets: ["ScreenReaderPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-spm.git", branch: "main")
    ],
    targets: [
        .target(
            name: "ScreenReaderPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-spm"),
                .product(name: "Cordova", package: "capacitor-spm")
            ],
            path: "ios/Sources/ScreenReaderPlugin"),
        .testTarget(
            name: "ScreenReaderPluginTests",
            dependencies: ["ScreenReaderPlugin"],
            path: "ios/Tests/ScreenReaderPluginTests")
    ]
)
