// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorToast",
    platforms: [.iOS(.v13)],
    products: [
        .library(
            name: "ToastPlugin",
            targets: ["ToastPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor6-spm-test.git", branch: "main")
    ],
    targets: [
        .target(
            name: "ToastPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor6-spm-test"),
                .product(name: "Cordova", package: "capacitor6-spm-test")
            ],
            path: "ios/Sources/ToastPlugin"),
        .testTarget(
            name: "ToastPluginTests",
            dependencies: ["ToastPlugin"],
            path: "ios/Tests/ToastPluginTests")
    ]
)