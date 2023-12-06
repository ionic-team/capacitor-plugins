// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorActionSheet",
    platforms: [.iOS(.v13)],
    products: [
        .library(
            name: "ActionSheetPlugin",
            targets: ["ActionSheetPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor6-spm-test.git", branch: "main")
    ],
    targets: [
        .target(
            name: "ActionSheetPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor6-spm-test"),
                .product(name: "Cordova", package: "capacitor6-spm-test")
            ],
            path: "ios/Sources/ActionSheetPlugin"),
        .testTarget(
            name: "ActionSheetPluginTests",
            dependencies: ["ActionSheetPlugin"],
            path: "ios/Tests/ActionSheetPluginTests")
    ]
)