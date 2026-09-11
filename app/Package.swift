// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorApp",
    platforms: [.iOS(.v16)],
    products: [
        .library(
            name: "CapacitorApp",
            targets: ["AppPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor.git", from: "9.0.0-alpha.7")
    ],
    targets: [
        .target(
            name: "AppPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor")
            ],
            path: "ios/Sources/AppPlugin"),
        .testTarget(
            name: "AppPluginTests",
            dependencies: ["AppPlugin"],
            path: "ios/Tests/AppPluginTests")
    ]
)
