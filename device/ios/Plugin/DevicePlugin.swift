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
        #if arch(i386) || arch(x86_64)
        isSimulator = true
        #endif

        let memUsed = implementation.getMemoryUsage()
        let diskFree = implementation.getFreeDiskSize() ?? 0
        let diskTotal = implementation.getTotalDiskSize() ?? 0

        var size = 0
        sysctlbyname("hw.machine", nil, &size, nil, 0)
        var machine = [CChar](repeating: 0, count: size)
        sysctlbyname("hw.machine", &machine, &size, nil, 0)
        let modelName = String(cString: machine)

        call.resolve([
            "memUsed": memUsed,
            "diskFree": diskFree,
            "diskTotal": diskTotal,
            "name": UIDevice.current.name,
            "model": UIDevice.current.model,
            "modelName": modelName,
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
