#import <Foundation/Foundation.h>
#import <Capacitor/Capacitor.h>

CAP_PLUGIN(CAPCameraPlugin, "Camera",
  CAP_PLUGIN_METHOD(getPhoto, CAPPluginReturnPromise);
)
