#import <Foundation/Foundation.h>
#import <Capacitor/Capacitor.h>

// Define the plugin using the CAP_PLUGIN Macro, and
// each method the plugin supports using the CAP_PLUGIN_METHOD macro.
CAP_PLUGIN(HapticsPlugin, "Haptics",
  CAP_PLUGIN_METHOD(impact, CAPPluginReturnPromise);
  CAP_PLUGIN_METHOD(notification, CAPPluginReturnPromise);
  CAP_PLUGIN_METHOD(selectionStart, CAPPluginReturnPromise);
  CAP_PLUGIN_METHOD(selectionChanged, CAPPluginReturnPromise);
  CAP_PLUGIN_METHOD(selectionEnd, CAPPluginReturnPromise);
  CAP_PLUGIN_METHOD(vibrate, CAPPluginReturnPromise);
)
