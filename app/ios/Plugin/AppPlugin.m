#import <Foundation/Foundation.h>
#import <Capacitor/Capacitor.h>

// Define the plugin using the CAP_PLUGIN Macro, and
// each method the plugin supports using the CAP_PLUGIN_METHOD macro.
CAP_PLUGIN(AppPlugin, "App",
           CAP_PLUGIN_METHOD(exitApp, CAPPluginReturnNone);
           CAP_PLUGIN_METHOD(getInfo, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(getLaunchUrl, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(getState, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(removeAllListeners, CAPPluginReturnNone);
)
