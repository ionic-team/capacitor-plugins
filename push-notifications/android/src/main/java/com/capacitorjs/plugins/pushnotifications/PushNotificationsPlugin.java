package com.capacitorjs.plugins.pushnotifications;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import androidx.core.app.NotificationCompat;
import com.getcapacitor.*;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import java.util.Arrays;
import org.json.JSONException;
import org.json.JSONObject;

@CapacitorPlugin(
    name = "PushNotifications",
    permissions = @Permission(strings = { Manifest.permission.POST_NOTIFICATIONS }, alias = PushNotificationsPlugin.PUSH_NOTIFICATIONS)
)
public class PushNotificationsPlugin extends Plugin {

    static final String PUSH_NOTIFICATIONS = "receive";

    public static Bridge staticBridge = null;
    public static RemoteMessage lastMessage = null;
    public NotificationManager notificationManager;
    public MessagingService firebaseMessagingService;
    private NotificationChannelManager notificationChannelManager;

    private static final String EVENT_TOKEN_CHANGE = "registration";
    private static final String EVENT_TOKEN_ERROR = "registrationError";

    public void load() {
        notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        firebaseMessagingService = new MessagingService();

        staticBridge = this.bridge;
        if (lastMessage != null) {
            fireNotification(lastMessage);
            lastMessage = null;
        }

        notificationChannelManager = new NotificationChannelManager(getActivity(), notificationManager, getConfig());
    }

    @Override
    protected void handleOnNewIntent(Intent data) {
        super.handleOnNewIntent(data);
        Bundle bundle = data.getExtras();
        if (bundle != null && bundle.containsKey("google.message_id")) {
            JSObject notificationJson = new JSObject();
            JSObject dataObject = new JSObject();
            for (String key : bundle.keySet()) {
                if (key.equals("google.message_id")) {
                    notificationJson.put("id", bundle.getString(key));
                } else {
                    String valueStr = bundle.getString(key);
                    dataObject.put(key, valueStr);
                }
            }
            notificationJson.put("data", dataObject);
            JSObject actionJson = new JSObject();
            actionJson.put("actionId", "tap");
            actionJson.put("notification", notificationJson);
            notifyListeners("pushNotificationActionPerformed", actionJson, true);
        }
    }

    @PluginMethod
    public void checkPermissions(PluginCall call) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            JSObject permissionsResultJSON = new JSObject();
            permissionsResultJSON.put("receive", "granted");
            call.resolve(permissionsResultJSON);
        } else {
            super.checkPermissions(call);
        }
    }

    @PluginMethod
    public void requestPermissions(PluginCall call) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            JSObject permissionsResultJSON = new JSObject();
            permissionsResultJSON.put("receive", "granted");
            call.resolve(permissionsResultJSON);
        } else {
            if (getPermissionState(PUSH_NOTIFICATIONS) != PermissionState.GRANTED) {
                requestPermissionForAlias(PUSH_NOTIFICATIONS, call, "permissionsCallback");
            }
        }
    }

    @PluginMethod
    public void register(PluginCall call) {
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        FirebaseMessaging
            .getInstance()
            .getToken()
            .addOnCompleteListener(
                task -> {
                    if (!task.isSuccessful()) {
                        sendError(task.getException().getLocalizedMessage());
                        return;
                    }
                    sendToken(task.getResult());
                }
            );
        call.resolve();
    }

    @PluginMethod
    public void unregister(PluginCall call) {
        FirebaseMessaging.getInstance().setAutoInitEnabled(false);
        FirebaseMessaging.getInstance().deleteToken();
        call.resolve();
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
                        extras.put(key, notification.extras.getString(key));
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

    public void sendToken(String token) {
        JSObject data = new JSObject();
        data.put("value", token);
        notifyListeners(EVENT_TOKEN_CHANGE, data, true);
    }

    public void sendError(String error) {
        JSObject data = new JSObject();
        data.put("error", error);
        notifyListeners(EVENT_TOKEN_ERROR, data, true);
    }

    public static void onNewToken(String newToken) {
        PushNotificationsPlugin pushPlugin = PushNotificationsPlugin.getPushNotificationsInstance();
        if (pushPlugin != null) {
            pushPlugin.sendToken(newToken);
        }
    }

    public static void sendRemoteMessage(RemoteMessage remoteMessage) {
        PushNotificationsPlugin pushPlugin = PushNotificationsPlugin.getPushNotificationsInstance();
        if (pushPlugin != null) {
            pushPlugin.fireNotification(remoteMessage);
        } else {
            lastMessage = remoteMessage;
        }
    }

    public void fireNotification(RemoteMessage remoteMessage) {
        JSObject remoteMessageData = new JSObject();

        JSObject data = new JSObject();
        remoteMessageData.put("id", remoteMessage.getMessageId());
        for (String key : remoteMessage.getData().keySet()) {
            Object value = remoteMessage.getData().get(key);
            data.put(key, value);
        }
        remoteMessageData.put("data", data);

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        if (notification != null) {
            String title = notification.getTitle();
            String body = notification.getBody();
            String[] presentation = getConfig().getArray("presentationOptions");
            if (presentation != null) {
                if (Arrays.asList(presentation).contains("alert")) {
                    Bundle bundle = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        try {
                            ApplicationInfo applicationInfo = getContext()
                                .getPackageManager()
                                .getApplicationInfo(
                                    getContext().getPackageName(),
                                    PackageManager.ApplicationInfoFlags.of(PackageManager.GET_META_DATA)
                                );
                            bundle = applicationInfo.metaData;
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else {
                        bundle = getBundleLegacy();
                    }

                    int pushIcon = android.R.drawable.ic_dialog_info;

                    if (bundle != null && bundle.getInt("com.google.firebase.messaging.default_notification_icon") != 0) {
                        pushIcon = bundle.getInt("com.google.firebase.messaging.default_notification_icon");
                    }
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(
                        getContext(),
                        NotificationChannelManager.FOREGROUND_NOTIFICATION_CHANNEL_ID
                    )
                        .setSmallIcon(pushIcon)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                    notificationManager.notify(0, builder.build());
                }
            }
            remoteMessageData.put("title", title);
            remoteMessageData.put("body", body);
            remoteMessageData.put("click_action", notification.getClickAction());

            Uri link = notification.getLink();
            if (link != null) {
                remoteMessageData.put("link", link.toString());
            }
        }

        notifyListeners("pushNotificationReceived", remoteMessageData, true);
    }

    public static PushNotificationsPlugin getPushNotificationsInstance() {
        if (staticBridge != null && staticBridge.getWebView() != null) {
            PluginHandle handle = staticBridge.getPlugin("PushNotifications");
            if (handle == null) {
                return null;
            }
            return (PushNotificationsPlugin) handle.getInstance();
        }
        return null;
    }

    @PermissionCallback
    private void permissionsCallback(PluginCall call) {
        this.checkPermissions(call);
    }

    @SuppressWarnings("deprecation")
    private Bundle getBundleLegacy() {
        try {
            ApplicationInfo applicationInfo = getContext()
                .getPackageManager()
                .getApplicationInfo(getContext().getPackageName(), PackageManager.GET_META_DATA);
            return applicationInfo.metaData;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
