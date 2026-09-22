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
        .package(url: "https://github.com/ionic-team/capacitor.git", from: "9.0.0-alpha.7")
    ],
    targets: [
        .target(
            name: "ToastPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor")
            ],
            path: "ios/Sources/ToastPlugin"),
        .testTarget(
            name: "ToastPluginTests",
            dependencies: ["ToastPlugin"],
            path: "ios/Tests/ToastPluginTests")
    ]
)
