#import <Foundation/Foundation.h>
#import <Capacitor/Capacitor.h>

// Define the plugin using the CAP_PLUGIN Macro, and
// each method the plugin supports using the CAP_PLUGIN_METHOD macro.
CAP_PLUGIN(GeolocationPlugin, "Geolocation",
  CAP_PLUGIN_METHOD(getCurrentPosition, CAPPluginReturnPromise);
  CAP_PLUGIN_METHOD(watchPosition, CAPPluginReturnCallback);
  CAP_PLUGIN_METHOD(clearWatch, CAPPluginReturnPromise);
  CAP_PLUGIN_METHOD(checkPermissions, CAPPluginReturnPromise);
  CAP_PLUGIN_METHOD(requestPermissions, CAPPluginReturnPromise);
)
