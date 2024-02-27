// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorApp",
    platforms: [.iOS(.v13)],
    products: [
        .library(
            name: "CapacitorApp",
            targets: ["AppPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-spm.git", branch: "main")
    ],
    targets: [
        .target(
            name: "AppPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-spm"),
                .product(name: "Cordova", package: "capacitor-spm")
            ],
            path: "ios/Sources/AppPlugin"),
        .testTarget(
            name: "AppPluginTests",
            dependencies: ["AppPlugin"],
            path: "ios/Tests/AppPluginTests")
    ]
)
