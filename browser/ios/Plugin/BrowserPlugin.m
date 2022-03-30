#import <Foundation/Foundation.h>
#import <Capacitor/Capacitor.h>

CAP_PLUGIN(CAPBrowserPlugin, "Browser",
  CAP_PLUGIN_METHOD(open, CAPPluginReturnPromise);
  CAP_PLUGIN_METHOD(close, CAPPluginReturnPromise);
  CAP_PLUGIN_METHOD(removeAllListeners, CAPPluginReturnPromise);
)
