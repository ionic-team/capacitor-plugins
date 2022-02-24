#import <Foundation/Foundation.h>
#import <Capacitor/Capacitor.h>

// Define the plugin using the CAP_PLUGIN Macro, and
// each method the plugin supports using the CAP_PLUGIN_METHOD macro.
CAP_PLUGIN(CapacitorGoogleMapsPlugin, "CapacitorGoogleMaps",
   CAP_PLUGIN_METHOD(create, CAPPluginReturnPromise);
   CAP_PLUGIN_METHOD(addMarker, CAPPluginReturnPromise);
   CAP_PLUGIN_METHOD(removeMarker, CAPPluginReturnPromise);
   CAP_PLUGIN_METHOD(destroy, CAPPluginReturnPromise);
)
