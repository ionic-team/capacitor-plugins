import Foundation
import Capacitor

@objc(DevicePlugin)
public class DevicePlugin: CAPPlugin {
    private let implementation = Device()

    @objc func getId(_ call: CAPPluginCall) {
        if let uuid = UIDevice.current.identifierForVendor {
            call.resolve([
                "uuid": uuid.uuidString
            ])
        } else {
            call.reject("Id not available")
        }
    }
    @objc func getInfo(_ call: CAPPluginCall) {
        var isSimulator = false
        #if targetEnvironment(simulator)
        isSimulator = true
        #endif

        let memUsed = implementation.getMemoryUsage()
        let diskFree = implementation.getFreeDiskSize() ?? 0
        let realDiskFree = implementation.getRealFreeDiskSize() ?? 0
        let diskTotal = implementation.getTotalDiskSize() ?? 0

        call.resolve([
            "memUsed": memUsed,
            "diskFree": diskFree,
            "diskTotal": diskTotal,
            "realDiskFree": realDiskFree,
            "realDiskTotal": diskTotal,
            "name": UIDevice.current.name,
            "model": UIDevice.current.model,
            "operatingSystem": "ios",
            "osVersion": UIDevice.current.systemVersion,
            "platform": "ios",
            "manufacturer": "Apple",
            "isVirtual": isSimulator,
            "webViewVersion": UIDevice.current.systemVersion
        ])
    }

    @objc func getBatteryInfo(_ call: CAPPluginCall) {
        UIDevice.current.isBatteryMonitoringEnabled = true

        call.resolve([
            "batteryLevel": UIDevice.current.batteryLevel,
            "isCharging": UIDevice.current.batteryState == .charging || UIDevice.current.batteryState == .full
        ])

        UIDevice.current.isBatteryMonitoringEnabled = false
    }

    @objc func getLanguageCode(_ call: CAPPluginCall) {
        let code = implementation.getLanguageCode()
        call.resolve([
            "value": code
        ])
    }

}
