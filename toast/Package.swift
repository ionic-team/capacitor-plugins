// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorToast",
    platforms: [.iOS(.v16)],
    products: [
        .library(
            name: "CapacitorToast",
            targets: ["ToastPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", from: "9.0.0-alpha.5")
    ],
    targets: [
        .target(
            name: "ToastPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/ToastPlugin"),
        .testTarget(
            name: "ToastPluginTests",
            dependencies: ["ToastPlugin"],
            path: "ios/Tests/ToastPluginTests")
    ]
)
