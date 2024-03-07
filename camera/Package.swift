// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorCamera",
    platforms: [.iOS(.v13)],
    products: [
        .library(
            name: "CapacitorCamera",
            targets: ["CameraPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-spm.git", branch: "main")
    ],
    targets: [
        .target(
            name: "CameraPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-spm"),
                .product(name: "Cordova", package: "capacitor-spm")
            ],
            path: "ios/Sources/CameraPlugin"),
        .testTarget(
            name: "CameraPluginTests",
            dependencies: ["CameraPlugin"],
            path: "ios/Tests/CameraPluginTests")
    ]
)
