#import <Foundation/Foundation.h>
#import <Capacitor/Capacitor.h>

// Define the plugin using the CAP_PLUGIN Macro, and
// each method the plugin supports using the CAP_PLUGIN_METHOD macro.
CAP_PLUGIN(DevicePlugin, "Device",
           CAP_PLUGIN_METHOD(getId, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(getInfo, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(getBatteryInfo, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(getLanguageCode, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(getLanguageTag, CAPPluginReturnPromise);
)
