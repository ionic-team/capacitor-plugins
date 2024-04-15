// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorClipboard",
    platforms: [.iOS(.v13)],
    products: [
        .library(
            name: "CapacitorClipboard",
            targets: ["ClipboardPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", branch: "main")
    ],
    targets: [
        .target(
            name: "ClipboardPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/ClipboardPlugin"),
        .testTarget(
            name: "ClipboardPluginTests",
            dependencies: ["ClipboardPlugin"],
            path: "ios/Tests/ClipboardPluginTests")
    ]
)
