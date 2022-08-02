#import <Foundation/Foundation.h>
#import <Capacitor/Capacitor.h>

CAP_PLUGIN(LocalNotificationsPlugin, "LocalNotifications",
    CAP_PLUGIN_METHOD(schedule, CAPPluginReturnPromise);
    CAP_PLUGIN_METHOD(requestPermissions, CAPPluginReturnPromise);
    CAP_PLUGIN_METHOD(checkPermissions, CAPPluginReturnPromise);
    CAP_PLUGIN_METHOD(cancel, CAPPluginReturnPromise);
    CAP_PLUGIN_METHOD(getPending, CAPPluginReturnPromise);
    CAP_PLUGIN_METHOD(registerActionTypes, CAPPluginReturnPromise);
    CAP_PLUGIN_METHOD(areEnabled, CAPPluginReturnPromise);
    CAP_PLUGIN_METHOD(getDeliveredNotifications, CAPPluginReturnPromise);
    CAP_PLUGIN_METHOD(removeAllDeliveredNotifications, CAPPluginReturnPromise);
    CAP_PLUGIN_METHOD(removeDeliveredNotifications, CAPPluginReturnPromise);
    CAP_PLUGIN_METHOD(createChannel, CAPPluginReturnPromise);
    CAP_PLUGIN_METHOD(deleteChannel, CAPPluginReturnPromise);
    CAP_PLUGIN_METHOD(listChannels, CAPPluginReturnPromise);
    CAP_PLUGIN_METHOD(removeAllListeners, CAPPluginReturnPromise);
)
