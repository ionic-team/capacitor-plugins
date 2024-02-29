// swift-tools-version: 5.9

import PackageDescription

let package = Package(
    name: "CapacitorKeyboard",
    platforms: [.iOS(.v13)],
    products: [
        .library(
            name: "CapacitorKeyboard",
            targets: ["KeyboardPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-spm.git", branch: "main")
    ],
    targets: [
        .target(
            name: "KeyboardPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-spm"),
                .product(name: "Cordova", package: "capacitor-spm")],
            path: "ios/Sources/KeyboardPlugin",
            publicHeadersPath: "include"),
        .testTarget(
            name: "KeyboardPluginTests",
            dependencies: ["KeyboardPlugin"],
            path: "ios/Tests/KeyboardPluginTests")
    ]
)
