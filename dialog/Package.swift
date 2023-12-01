// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorDialog",
    platforms: [.iOS(.v13)],
    products: [
        .library(
            name: "DialogPlugin",
            targets: ["DialogPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor6-spm-test.git", branch: "main")
    ],
    targets: [
        .target(
            name: "DialogPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor6-spm-test"),
                .product(name: "Cordova", package: "capacitor6-spm-test")
            ],
            path: "ios/Sources/DialogPlugin"),
        .testTarget(
            name: "DialogPluginTests",
            dependencies: ["DialogPlugin"],
            path: "ios/Tests/DialogPluginTests")
    ]
)
