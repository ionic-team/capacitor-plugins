// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorGoogleMaps",
    platforms: [.iOS(.v13)],
    products: [
        .library(
            name: "CapacitorGoogleMapsPlugin",
            targets: ["CapacitorGoogleMapsPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor6-spm-test.git", branch: "main"),
        .package(url: "https://github.com/yourparkingspace/googlemaps-spm.git", from: "7.1.0"),
        .package(url: "https://github.com/googlemaps/google-maps-ios-utils.git", from: "4.2.2")
    ],
    targets: [
        .target(
            name: "CapacitorGoogleMapsPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor6-spm-test"),
                .product(name: "Cordova", package: "capacitor6-spm-test")
            ],
            path: "ios/Sources/CapacitorGoogleMapsPlugin"),
        .testTarget(
            name: "CapacitorGoogleMapsPluginTests",
            dependencies: ["CapacitorGoogleMapsPlugin"],
            path: "ios/Tests/CapacitorGoogleMapsPluginTests")
    ]
)