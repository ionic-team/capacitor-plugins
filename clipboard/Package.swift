// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorClipboard",
    platforms: [.iOS(.v13)],
    products: [
        .library(
            name: "ClipboardPlugin",
            targets: ["ClipboardPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-spm.git", branch: "main")
    ],
    targets: [
        .target(
            name: "ClipboardPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-spm"),
                .product(name: "Cordova", package: "capacitor-spm")
            ],
            path: "ios/Sources/ClipboardPlugin"),
        .testTarget(
            name: "ClipboardPluginTests",
            dependencies: ["ClipboardPlugin"],
            path: "ios/Tests/ClipboardPluginTests")
    ]
)
