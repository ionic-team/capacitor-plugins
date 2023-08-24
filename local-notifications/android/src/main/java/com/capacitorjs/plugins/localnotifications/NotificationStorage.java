package com.capacitorjs.plugins.localnotifications;

import android.content.Context;
import android.content.SharedPreferences;
import com.getcapacitor.JSObject;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.JSONException;

/**
 * Class used to abstract storage for notification data
 */
public class NotificationStorage {

    // Key for private preferences
    private static final String NOTIFICATION_STORE_ID = "NOTIFICATION_STORE";

    // Key used to save action types
    private static final String ACTION_TYPES_ID = "ACTION_TYPE_STORE";

    private static final String ID_KEY = "notificationIds";

    private Context context;

    public NotificationStorage(Context context) {
        this.context = context;
    }

    /**
     * Persist the id of currently scheduled notification
     */
    public void appendNotifications(List<LocalNotification> localNotifications) {
        SharedPreferences storage = getStorage(NOTIFICATION_STORE_ID);
        SharedPreferences.Editor editor = storage.edit();
        for (LocalNotification request : localNotifications) {
            if (request.isScheduled()) {
                String key = request.getId().toString();
                editor.putString(key, request.getSource());
            }
        }
        editor.apply();
    }

    public List<String> getSavedNotificationIds() {
        SharedPreferences storage = getStorage(NOTIFICATION_STORE_ID);
        Map<String, ?> all = storage.getAll();
        if (all != null) {
            return new ArrayList<>(all.keySet());
        }
        return new ArrayList<>();
    }

    public List<LocalNotification> getSavedNotifications() {
        SharedPreferences storage = getStorage(NOTIFICATION_STORE_ID);
        Map<String, ?> all = storage.getAll();
        if (all != null) {
            ArrayList<LocalNotification> notifications = new ArrayList<>();
            for (String key : all.keySet()) {
                String notificationString = (String) all.get(key);
                JSObject jsNotification = getNotificationFromJSONString(notificationString);
                if (jsNotification != null) {
                    try {
                        LocalNotification notification = LocalNotification.buildNotificationFromJSObject(jsNotification);
                        notifications.add(notification);
                    } catch (ParseException ex) {}
                }
            }

            return notifications;
        }

        return new ArrayList<>();
    }

    public JSObject getNotificationFromJSONString(String notificationString) {
        if (notificationString == null) {
            return null;
        }

        JSObject jsNotification;
        try {
            jsNotification = new JSObject(notificationString);
        } catch (JSONException ex) {
            return null;
        }

        return jsNotification;
    }

    public JSObject getSavedNotificationAsJSObject(String key) {
        SharedPreferences storage = getStorage(NOTIFICATION_STORE_ID);
        String notificationString;
        try {
            notificationString = storage.getString(key, null);
        } catch (ClassCastException ex) {
            return null;
        }

        if (notificationString == null) {
            return null;
        }

        JSObject jsNotification;
        try {
            jsNotification = new JSObject(notificationString);
        } catch (JSONException ex) {
            return null;
        }

        return jsNotification;
    }

    public LocalNotification getSavedNotification(String key) {
        JSObject jsNotification = getSavedNotificationAsJSObject(key);
        if (jsNotification == null) {
            return null;
        }

        LocalNotification notification;
        try {
            notification = LocalNotification.buildNotificationFromJSObject(jsNotification);
        } catch (ParseException ex) {
            return null;
        }

        return notification;
    }

    /**
     * Remove the stored notifications
     */
    public void deleteNotification(String id) {
        SharedPreferences.Editor editor = getStorage(NOTIFICATION_STORE_ID).edit();
        editor.remove(id);
        editor.apply();
    }

    /**
     * Shared private preferences for the application.
     */
    private SharedPreferences getStorage(String key) {
        return context.getSharedPreferences(key, Context.MODE_PRIVATE);
    }

    /**
     * Writes new action types (actions that being displayed in notification) to storage.
     * Write will override previous data.
     *
     * @param typesMap - map with groupId and actionArray assigned to group
     */
    public void writeActionGroup(Map<String, NotificationAction[]> typesMap) {
        Set<String> typesIds = typesMap.keySet();
        for (String id : typesIds) {
            SharedPreferences.Editor editor = getStorage(ACTION_TYPES_ID + id).edit();
            editor.clear();
            NotificationAction[] notificationActions = typesMap.get(id);
            editor.putInt("count", notificationActions.length);
            for (int i = 0; i < notificationActions.length; i++) {
                editor.putString("id" + i, notificationActions[i].getId());
                editor.putString("title" + i, notificationActions[i].getTitle());
                editor.putBoolean("input" + i, notificationActions[i].isInput());
            }
            editor.apply();
        }
    }

    /**
     * Retrieve array of notification actions per ActionTypeId
     *
     * @param forId - id of the group
     */
    public NotificationAction[] getActionGroup(String forId) {
        SharedPreferences storage = getStorage(ACTION_TYPES_ID + forId);
        int count = storage.getInt("count", 0);
        NotificationAction[] actions = new NotificationAction[count];
        for (int i = 0; i < count; i++) {
            String id = storage.getString("id" + i, "");
            String title = storage.getString("title" + i, "");
            Boolean input = storage.getBoolean("input" + i, false);
            actions[i] = new NotificationAction(id, title, input);
        }
        return actions;
    }
}
