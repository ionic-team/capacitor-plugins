// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorToast",
    platforms: [.iOS(.v13)],
    products: [
        .library(
            name: "CapacitorToast",
            targets: ["ToastPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-spm.git", branch: "main")
    ],
    targets: [
        .target(
            name: "ToastPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-spm"),
                .product(name: "Cordova", package: "capacitor-spm")
            ],
            path: "ios/Sources/ToastPlugin"),
        .testTarget(
            name: "ToastPluginTests",
            dependencies: ["ToastPlugin"],
            path: "ios/Tests/ToastPluginTests")
    ]
)
