package com.capacitorjs.plugins.localnotifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.service.notification.StatusBarNotification;
import com.getcapacitor.Bridge;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginHandle;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@CapacitorPlugin(name = "LocalNotifications", permissions = @Permission(strings = {}, alias = "display"))
public class LocalNotificationsPlugin extends Plugin {

    private static Bridge staticBridge = null;
    private LocalNotificationManager manager;
    public NotificationManager notificationManager;
    private NotificationStorage notificationStorage;
    private NotificationChannelManager notificationChannelManager;

    @Override
    public void load() {
        super.load();
        notificationStorage = new NotificationStorage(getContext());
        manager = new LocalNotificationManager(notificationStorage, getActivity(), getContext(), this.bridge.getConfig());
        manager.createNotificationChannel();
        notificationChannelManager = new NotificationChannelManager(getActivity());
        notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        staticBridge = this.bridge;
    }

    @Override
    protected void handleOnNewIntent(Intent data) {
        super.handleOnNewIntent(data);
        if (!Intent.ACTION_MAIN.equals(data.getAction())) {
            return;
        }
        JSObject dataJson = manager.handleNotificationActionPerformed(data, notificationStorage);
        if (dataJson != null) {
            notifyListeners("localNotificationActionPerformed", dataJson, true);
        }
    }

    /**
     * Schedule a notification call from JavaScript
     * Creates local notification in system.
     */
    @PluginMethod
    public void schedule(PluginCall call) {
        List<LocalNotification> localNotifications = LocalNotification.buildNotificationList(call);
        if (localNotifications == null) {
            return;
        }
        JSONArray ids = manager.schedule(call, localNotifications);
        if (ids != null) {
            notificationStorage.appendNotifications(localNotifications);
            JSObject result = new JSObject();
            JSArray jsArray = new JSArray();
            for (int i = 0; i < ids.length(); i++) {
                try {
                    JSObject notification = new JSObject().put("id", ids.getInt(i));
                    jsArray.put(notification);
                } catch (Exception ex) {}
            }
            result.put("notifications", jsArray);
            call.resolve(result);
        }
    }

    @PluginMethod
    public void cancel(PluginCall call) {
        manager.cancel(call);
    }

    @PluginMethod
    public void getPending(PluginCall call) {
        List<LocalNotification> notifications = notificationStorage.getSavedNotifications();
        JSObject result = LocalNotification.buildLocalNotificationPendingList(notifications);
        call.resolve(result);
    }

    @PluginMethod
    public void registerActionTypes(PluginCall call) {
        JSArray types = call.getArray("types");
        Map<String, NotificationAction[]> typesArray = NotificationAction.buildTypes(types);
        notificationStorage.writeActionGroup(typesArray);
        call.resolve();
    }

    @PluginMethod
    public void areEnabled(PluginCall call) {
        JSObject data = new JSObject();
        data.put("value", manager.areNotificationsEnabled());
        call.resolve(data);
    }

    @PluginMethod
    public void getDeliveredNotifications(PluginCall call) {
        JSArray notifications = new JSArray();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarNotification[] activeNotifications = notificationManager.getActiveNotifications();

            for (StatusBarNotification notif : activeNotifications) {
                JSObject jsNotif = new JSObject();

                jsNotif.put("id", notif.getId());
                jsNotif.put("tag", notif.getTag());

                Notification notification = notif.getNotification();
                if (notification != null) {
                    jsNotif.put("title", notification.extras.getCharSequence(Notification.EXTRA_TITLE));
                    jsNotif.put("body", notification.extras.getCharSequence(Notification.EXTRA_TEXT));
                    jsNotif.put("group", notification.getGroup());
                    jsNotif.put("groupSummary", 0 != (notification.flags & Notification.FLAG_GROUP_SUMMARY));

                    JSObject extras = new JSObject();

                    for (String key : notification.extras.keySet()) {
                        extras.put(key, notification.extras.get(key));
                    }

                    jsNotif.put("data", extras);
                }

                notifications.put(jsNotif);
            }
        }

        JSObject result = new JSObject();
        result.put("notifications", notifications);
        call.resolve(result);
    }

    @PluginMethod
    public void removeDeliveredNotifications(PluginCall call) {
        JSArray notifications = call.getArray("notifications");

        try {
            for (Object o : notifications.toList()) {
                if (o instanceof JSONObject) {
                    JSObject notif = JSObject.fromJSONObject((JSONObject) o);
                    String tag = notif.getString("tag");
                    Integer id = notif.getInteger("id");

                    if (tag == null) {
                        notificationManager.cancel(id);
                    } else {
                        notificationManager.cancel(tag, id);
                    }
                } else {
                    call.reject("Expected notifications to be a list of notification objects");
                }
            }
        } catch (JSONException e) {
            call.reject(e.getMessage());
        }

        call.resolve();
    }

    @PluginMethod
    public void removeAllDeliveredNotifications(PluginCall call) {
        notificationManager.cancelAll();
        call.resolve();
    }

    @PluginMethod
    public void createChannel(PluginCall call) {
        notificationChannelManager.createChannel(call);
    }

    @PluginMethod
    public void deleteChannel(PluginCall call) {
        notificationChannelManager.deleteChannel(call);
    }

    @PluginMethod
    public void listChannels(PluginCall call) {
        notificationChannelManager.listChannels(call);
    }

    @PluginMethod
    public void checkPermissions(PluginCall call) {
        JSObject permissionsResultJSON = new JSObject();
        permissionsResultJSON.put("display", getNotificationPermissionText());
        call.resolve(permissionsResultJSON);
    }

    @PluginMethod
    public void requestPermissions(PluginCall call) {
        JSObject permissionsResultJSON = new JSObject();
        permissionsResultJSON.put("display", getNotificationPermissionText());
        call.resolve(permissionsResultJSON);
    }

    private String getNotificationPermissionText() {
        if (manager.areNotificationsEnabled()) {
            return "granted";
        } else {
            return "denied";
        }
    }

    public static void fireReceived(JSObject notification) {
        LocalNotificationsPlugin localNotificationsPlugin = LocalNotificationsPlugin.getLocalNotificationsInstance();
        if (localNotificationsPlugin != null) {
            localNotificationsPlugin.notifyListeners("localNotificationReceived", notification, true);
        }
    }

    public static LocalNotificationsPlugin getLocalNotificationsInstance() {
        if (staticBridge != null && staticBridge.getWebView() != null) {
            PluginHandle handle = staticBridge.getPlugin("LocalNotifications");
            if (handle == null) {
                return null;
            }
            return (LocalNotificationsPlugin) handle.getInstance();
        }
        return null;
    }
}
