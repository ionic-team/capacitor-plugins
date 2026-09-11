// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorDialog",
    platforms: [.iOS(.v16)],
    products: [
        .library(
            name: "CapacitorDialog",
            targets: ["DialogPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor.git", from: "9.0.0-alpha.7")
    ],
    targets: [
        .target(
            name: "DialogPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor")
            ],
            path: "ios/Sources/DialogPlugin"),
        .testTarget(
            name: "DialogPluginTests",
            dependencies: ["DialogPlugin"],
            path: "ios/Tests/DialogPluginTests")
    ]
)
