#import <Foundation/Foundation.h>
#import <Capacitor/Capacitor.h>

// Define the plugin using the CAP_PLUGIN Macro, and
// each method the plugin supports using the CAP_PLUGIN_METHOD macro.
CAP_PLUGIN(HapticsPlugin, "Haptics",
  CAP_PLUGIN_METHOD(impact, CAPPluginReturnNone);
  CAP_PLUGIN_METHOD(notification, CAPPluginReturnNone);
  CAP_PLUGIN_METHOD(selectionStart, CAPPluginReturnNone);
  CAP_PLUGIN_METHOD(selectionChanged, CAPPluginReturnNone);
  CAP_PLUGIN_METHOD(selectionEnd, CAPPluginReturnNone);
  CAP_PLUGIN_METHOD(vibrate, CAPPluginReturnNone);
)