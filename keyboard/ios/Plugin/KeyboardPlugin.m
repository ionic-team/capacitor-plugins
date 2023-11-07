#import <Foundation/Foundation.h>
#import <Capacitor/Capacitor.h>

// Define the plugin using the CAP_PLUGIN Macro, and
// each method the plugin supports using the CAP_PLUGIN_METHOD macro.
CAP_PLUGIN(KeyboardPlugin, "Keyboard",
           CAP_PLUGIN_METHOD(show, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(hide, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(setAccessoryBarVisible, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(setStyle, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(setResizeMode, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(getResizeMode, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(setScroll, CAPPluginReturnPromise);
)
