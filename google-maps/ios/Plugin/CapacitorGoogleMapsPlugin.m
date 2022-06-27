#import <Foundation/Foundation.h>
#import <Capacitor/Capacitor.h>

// Define the plugin using the CAP_PLUGIN Macro, and
// each method the plugin supports using the CAP_PLUGIN_METHOD macro.
CAP_PLUGIN(CapacitorGoogleMapsPlugin, "CapacitorGoogleMaps",
   CAP_PLUGIN_METHOD(create, CAPPluginReturnPromise);
   CAP_PLUGIN_METHOD(addMarker, CAPPluginReturnPromise);
   CAP_PLUGIN_METHOD(addMarkers, CAPPluginReturnPromise);
   CAP_PLUGIN_METHOD(removeMarker, CAPPluginReturnPromise);
   CAP_PLUGIN_METHOD(removeMarkers, CAPPluginReturnPromise);
   CAP_PLUGIN_METHOD(enableClustering, CAPPluginReturnPromise);
   CAP_PLUGIN_METHOD(disableClustering, CAPPluginReturnPromise);
   CAP_PLUGIN_METHOD(destroy, CAPPluginReturnPromise);
   CAP_PLUGIN_METHOD(setCamera, CAPPluginReturnPromise);
   CAP_PLUGIN_METHOD(setMapType, CAPPluginReturnPromise);
   CAP_PLUGIN_METHOD(enableIndoorMaps, CAPPluginReturnPromise);
   CAP_PLUGIN_METHOD(enableTrafficLayer, CAPPluginReturnPromise);
   CAP_PLUGIN_METHOD(enableAccessibilityElements, CAPPluginReturnPromise);
   CAP_PLUGIN_METHOD(enableCurrentLocation, CAPPluginReturnPromise);
   CAP_PLUGIN_METHOD(setPadding, CAPPluginReturnPromise);           
   CAP_PLUGIN_METHOD(onScroll, CAPPluginReturnPromise);
   CAP_PLUGIN_METHOD(getMapBounds, CAPPluginReturnPromise);
)
