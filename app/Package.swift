// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorAppPlugin",
    platforms: [.iOS(.v13)],
    products: [
        .library(
            name: "AppPlugin",
            targets: ["AppPlugin", "Capacitor", "Cordova"]),
    ],
    targets: [
        .target(
            name: "AppPlugin", 
            dependencies: ["Capacitor", "Cordova"],
            path: "ios/Sources/AppPlugin"),
        .binaryTarget(
            name: "Capacitor",
            path: "../node_modules/@capacitor/ios/Frameworks/Capacitor.xcframework"
        ),
        .binaryTarget(
            name: "Cordova",
            path: "../node_modules/@capacitor/ios/Frameworks/Cordova.xcframework"
        ),
        .testTarget(
            name: "AppPluginTests",
            dependencies: ["AppPlugin"]),
    ]
)
