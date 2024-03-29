// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorDialog",
    platforms: [.iOS(.v13)],
    products: [
        .library(
            name: "CapacitorDialog",
            targets: ["DialogPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-spm.git", branch: "main")
    ],
    targets: [
        .target(
            name: "DialogPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-spm"),
                .product(name: "Cordova", package: "capacitor-spm")
            ],
            path: "ios/Sources/DialogPlugin"),
        .testTarget(
            name: "DialogPluginTests",
            dependencies: ["DialogPlugin"],
            path: "ios/Tests/DialogPluginTests")
    ]
)
