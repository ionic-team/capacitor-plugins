package com.capacitorjs.plugins.localnotifications;

import static android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.service.notification.StatusBarNotification;
import androidx.activity.result.ActivityResult;
import androidx.core.app.NotificationCompat;
import com.getcapacitor.Bridge;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.PermissionState;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginHandle;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.ActivityCallback;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@CapacitorPlugin(
    name = "LocalNotifications",
    permissions = @Permission(strings = { Manifest.permission.POST_NOTIFICATIONS }, alias = LocalNotificationsPlugin.LOCAL_NOTIFICATIONS)
)
public class LocalNotificationsPlugin extends Plugin {

    static final String LOCAL_NOTIFICATIONS = "display";
    private static final String LIVE_ACTIVITY_CHANNEL_ID = "live_activity";

    private static Bridge staticBridge = null;
    private LocalNotificationManager manager;
    public NotificationManager notificationManager;
    private NotificationStorage notificationStorage;
    private NotificationChannelManager notificationChannelManager;
    
    // Store active live activities: activityId -> LiveActivityConfig
    private final Map<String, LiveActivityConfig> activeLiveActivities = new HashMap<>();
    
    // Configuration d'une Live Activity pour pouvoir la mettre à jour
    private static class LiveActivityConfig {
        int notificationId;
        String title;
        String message;
        String channelId;
        String actionTypeId;
        JSObject timer;
        long startTimestamp;
        long maxDurationMs;
        boolean hasProgressService;
        
        LiveActivityConfig(int notificationId, String title, String message, String channelId, 
                          String actionTypeId, JSObject timer, long startTimestamp, long maxDurationMs,
                          boolean hasProgressService) {
            this.notificationId = notificationId;
            this.title = title;
            this.message = message;
            this.channelId = channelId;
            this.actionTypeId = actionTypeId;
            this.timer = timer;
            this.startTimestamp = startTimestamp;
            this.maxDurationMs = maxDurationMs;
            this.hasProgressService = hasProgressService;
        }
    }

    @Override
    public void load() {
        super.load();
        notificationStorage = new NotificationStorage(getContext());
        manager = new LocalNotificationManager(notificationStorage, getActivity(), getContext(), this.bridge.getConfig());
        manager.createNotificationChannel();
        notificationChannelManager = new NotificationChannelManager(getActivity());
        notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        staticBridge = this.bridge;
        
        // Handle notification action from launch intent (when app was closed)
        handleLaunchIntent();
    }
    
    /**
     * Handle notification action from the launch intent.
     * This is called when the app is launched from a notification action
     * while the app was completely closed.
     */
    private void handleLaunchIntent() {
        if (getActivity() == null) return;
        
        Intent launchIntent = getActivity().getIntent();
        if (launchIntent == null) return;
        
        // Check if this intent contains notification action data
        if (launchIntent.hasExtra(LocalNotificationManager.ACTION_INTENT_KEY)) {
            // Delay processing to ensure bridge is ready
            getActivity().runOnUiThread(() -> {
                // Small delay to ensure JavaScript is ready
                new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                    JSObject dataJson = manager.handleNotificationActionPerformed(launchIntent, notificationStorage);
                    if (dataJson != null) {
                        notifyListeners("localNotificationActionPerformed", dataJson, true);
                    }
                }, 500);
            });
        }
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
    
    @Override
    protected void handleOnDestroy() {
        super.handleOnDestroy();
        // End all Live Activities when app is destroyed
        endAllLiveActivities();
    }
    
    /**
     * End all active Live Activities.
     * Called when the app is destroyed to clean up ongoing notifications.
     */
    private void endAllLiveActivities() {
        // Stop the timer progress service
        TimerProgressService.stopTimer(getContext());
        
        // Cancel all ongoing notifications
        for (String activityId : activeLiveActivities.keySet()) {
            LiveActivityConfig config = activeLiveActivities.get(activityId);
            if (config != null) {
                notificationManager.cancel(config.notificationId);
                cancelTimerEndAlarm(activityId);
            }
        }
        activeLiveActivities.clear();
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
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            JSObject permissionsResultJSON = new JSObject();
            permissionsResultJSON.put("display", getNotificationPermissionText());
            call.resolve(permissionsResultJSON);
        } else {
            super.checkPermissions(call);
        }
    }

    @PluginMethod
    public void requestPermissions(PluginCall call) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || getPermissionState(LOCAL_NOTIFICATIONS) == PermissionState.GRANTED) {
            JSObject permissionsResultJSON = new JSObject();
            permissionsResultJSON.put("display", getNotificationPermissionText());
            call.resolve(permissionsResultJSON);
        } else {
            requestPermissionForAlias(LOCAL_NOTIFICATIONS, call, "permissionsCallback");
        }
    }

    @PluginMethod
    public void changeExactNotificationSetting(PluginCall call) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            startActivityForResult(
                call,
                new Intent(ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse("package:" + getActivity().getPackageName())),
                "alarmPermissionsCallback"
            );
        } else {
            checkExactNotificationSetting(call);
        }
    }

    @PluginMethod
    public void checkExactNotificationSetting(PluginCall call) {
        JSObject permissionsResultJSON = new JSObject();
        permissionsResultJSON.put("exact_alarm", getExactAlarmPermissionText());

        call.resolve(permissionsResultJSON);
    }

    @PermissionCallback
    private void permissionsCallback(PluginCall call) {
        JSObject permissionsResultJSON = new JSObject();
        permissionsResultJSON.put("display", getNotificationPermissionText());

        call.resolve(permissionsResultJSON);
    }

    @ActivityCallback
    private void alarmPermissionsCallback(PluginCall call, ActivityResult result) {
        checkExactNotificationSetting(call);
    }

    private String getNotificationPermissionText() {
        if (manager.areNotificationsEnabled()) {
            return "granted";
        } else {
            return "denied";
        }
    }

    private String getExactAlarmPermissionText() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
            if (alarmManager.canScheduleExactAlarms()) {
                return "granted";
            } else {
                return "denied";
            }
        }

        return "granted";
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

    /**
     * Public method to notify listeners from external classes (like TimerEndReceiver).
     * This is needed because notifyListeners is protected in the parent Plugin class.
     */
    public void fireTimerEnded(String activityId) {
        JSObject data = new JSObject();
        data.put("activityId", activityId);
        notifyListeners("liveActivityEnded", data, true);
    }

    // ============================================
    // LIVE ACTIVITY / TIMER NOTIFICATION METHODS
    // ============================================

    /**
     * Start a Live Activity (on Android, this is a notification with native chronometer).
     * The system handles timer display automatically - works in background.
     */
    @PluginMethod
    public void startLiveActivity(PluginCall call) {
        String id = call.getString("id");
        String title = call.getString("title");
        String message = call.getString("message");
        String actionTypeId = call.getString("actionTypeId");

        if (id == null || title == null) {
            call.reject("id and title are required");
            return;
        }

        // Generate notification ID from string ID
        int notificationId = Math.abs(id.hashCode());

        // Get channel ID, default to live_activity channel
        String channelId = call.getString("channelId", LIVE_ACTIVITY_CHANNEL_ID);

        // Ensure the channel exists
        createLiveActivityChannelIfNeeded();

        // Check if initial vibration is requested
        Boolean shouldVibrate = call.getBoolean("vibrate", true);
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), channelId)
            .setContentTitle(title)
            .setContentText(message != null ? message : "")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setOngoing(true)
            .setAutoCancel(false)
            .setPriority(NotificationCompat.PRIORITY_HIGH);
        
        // Vibrate on first show, then silent for updates
        if (shouldVibrate) {
            builder.setVibrate(new long[]{0, 300, 200, 300}); // Vibration pattern
            builder.setDefaults(NotificationCompat.DEFAULT_LIGHTS);
        } else {
            builder.setOnlyAlertOnce(true);
        }

        // Configure timer if present
        JSObject timer = call.getObject("timer");
        boolean hasTimer = false;
        long startTimestamp = System.currentTimeMillis();
        long maxDurationMs = 0;
        boolean hasProgressService = false;
        
        if (timer != null) {
            String mode = timer.getString("mode", "countdown");
            // Use optLong to avoid JSONException - returns 0 if not found
            long targetTimestamp = timer.optLong("targetTimestamp", 0);
            
            // Get maxDuration for elapsed timers
            maxDurationMs = timer.optLong("maxDurationMs", 0);
            startTimestamp = timer.optLong("startTimestamp", System.currentTimeMillis());

            if (targetTimestamp > 0) {
                hasTimer = true;
                builder.setUsesChronometer(true);
                builder.setWhen(targetTimestamp);
                builder.setShowWhen(true);

                // API 24+ for countdown mode
                if ("countdown".equals(mode) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    builder.setChronometerCountDown(true);
                }

                // Optional: schedule alarm for when timer ends
                Boolean alertOnEnd = timer.getBoolean("alertOnEnd", false);
                if (alertOnEnd) {
                    // Use alertTimestamp if provided (for elapsed timers with maxDuration),
                    // otherwise use targetTimestamp (for countdown timers)
                    long alertTimestamp = timer.optLong("alertTimestamp", targetTimestamp);
                    scheduleTimerEndAlarm(id, alertTimestamp);
                }
                
                // For elapsed timers with maxDuration, start the background progress service
                if ("elapsed".equals(mode) && maxDurationMs > 0) {
                    hasProgressService = true;
                    TimerProgressService.startTimer(
                        getContext(),
                        id,
                        notificationId,
                        title,
                        message,
                        channelId,
                        actionTypeId,
                        startTimestamp,
                        maxDurationMs
                    );
                }
            }
        }

        // Configure progress bar if present
        JSObject progress = call.getObject("progress");
        if (progress != null) {
            int max = progress.getInteger("max", 100);
            int current = progress.getInteger("current", 0);
            boolean indeterminate = progress.getBoolean("indeterminate", false);
            builder.setProgress(max, current, indeterminate);
        }

        // Use BigTextStyle to ensure message is visible even with chronometer
        if (hasTimer || progress != null) {
            String displayText = message != null ? message : "";
            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle()
                .bigText(displayText)
                .setBigContentTitle(title);
            builder.setStyle(bigTextStyle);
        }

        // Add action buttons if actionTypeId is provided
        if (actionTypeId != null) {
            addActionsToLiveActivity(builder, id, notificationId, actionTypeId);
        }

        // Add content intent (open app when notification is tapped)
        addContentIntentToLiveActivity(builder, id, notificationId);

        // Store configuration for updates BEFORE showing notification
        activeLiveActivities.put(id, new LiveActivityConfig(
            notificationId, title, message, channelId, actionTypeId, timer, 
            startTimestamp, maxDurationMs, hasProgressService
        ));

        // Show the notification only if the service won't handle it
        // When hasProgressService is true, the foreground service shows the notification
        if (!hasProgressService) {
            notificationManager.notify(notificationId, builder.build());
        }

        JSObject result = new JSObject();
        result.put("activityId", id);
        result.put("notificationId", notificationId);
        call.resolve(result);
    }

    /**
     * Add action buttons to a Live Activity notification.
     */
    private void addActionsToLiveActivity(NotificationCompat.Builder builder, String activityId, int notificationId, String actionTypeId) {
        NotificationAction[] actionGroup = notificationStorage.getActionGroup(actionTypeId);
        if (actionGroup == null) {
            return;
        }

        int flags = PendingIntent.FLAG_CANCEL_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            flags = flags | PendingIntent.FLAG_MUTABLE;
        }

        for (NotificationAction action : actionGroup) {
            Intent actionIntent = buildLiveActivityActionIntent(activityId, notificationId, action.getId());
            PendingIntent actionPendingIntent = PendingIntent.getActivity(
                getContext(),
                notificationId + action.getId().hashCode(),
                actionIntent,
                flags
            );

            NotificationCompat.Action.Builder actionBuilder = new NotificationCompat.Action.Builder(
                android.R.drawable.ic_menu_send, // Default icon
                action.getTitle(),
                actionPendingIntent
            );

            builder.addAction(actionBuilder.build());
        }
    }

    /**
     * Add content intent (tap to open) to a Live Activity notification.
     */
    private void addContentIntentToLiveActivity(NotificationCompat.Builder builder, String activityId, int notificationId) {
        Intent intent = buildLiveActivityActionIntent(activityId, notificationId, "tap");
        int flags = PendingIntent.FLAG_CANCEL_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            flags = flags | PendingIntent.FLAG_MUTABLE;
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), notificationId, intent, flags);
        builder.setContentIntent(pendingIntent);
    }

    /**
     * Build an intent for Live Activity actions.
     */
    private Intent buildLiveActivityActionIntent(String activityId, int notificationId, String actionId) {
        String packageName = getContext().getPackageName();
        Intent intent = getContext().getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent == null) {
            intent = new Intent();
        }
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(LocalNotificationManager.NOTIFICATION_INTENT_KEY, notificationId);
        intent.putExtra(LocalNotificationManager.ACTION_INTENT_KEY, actionId);
        intent.putExtra("liveActivityId", activityId);
        intent.putExtra(LocalNotificationManager.NOTIFICATION_IS_REMOVABLE_KEY, true);
        
        // Build a minimal notification JSON for the action handler
        JSObject notificationObj = new JSObject();
        notificationObj.put("id", notificationId);
        notificationObj.put("liveActivityId", activityId);
        intent.putExtra(LocalNotificationManager.NOTIFICATION_OBJ_INTENT_KEY, notificationObj.toString());
        
        return intent;
    }

    /**
     * Update a Live Activity content.
     */
    @PluginMethod
    public void updateLiveActivity(PluginCall call) {
        String id = call.getString("id");
        if (id == null) {
            call.reject("id is required");
            return;
        }

        LiveActivityConfig config = activeLiveActivities.get(id);
        if (config == null) {
            call.reject("No active Live Activity with id: " + id);
            return;
        }

        String title = call.getString("title");
        String message = call.getString("message");
        
        // Use stored config or provided values
        String finalTitle = title != null ? title : config.title;
        String finalMessage = message != null ? message : (config.message != null ? config.message : "");
        
        // Check if vibration is requested (for alert state)
        Boolean shouldVibrate = call.getBoolean("vibrate", false);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), config.channelId)
            .setContentTitle(finalTitle)
            .setContentText(finalMessage)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setOngoing(true)
            .setAutoCancel(false)
            .setPriority(NotificationCompat.PRIORITY_HIGH);
        
        // Vibrate if requested (e.g., for alert state), otherwise silent
        if (shouldVibrate) {
            builder.setVibrate(new long[]{0, 500, 250, 500}); // Alert vibration pattern (longer)
            builder.setDefaults(NotificationCompat.DEFAULT_LIGHTS);
        } else {
            builder.setOnlyAlertOnce(true);
        }

        // Restore chronometer from config
        if (config.timer != null) {
            String mode = config.timer.getString("mode", "countdown");
            long targetTimestamp = config.timer.optLong("targetTimestamp", 0);
            
            if (targetTimestamp > 0) {
                builder.setUsesChronometer(true);
                builder.setWhen(targetTimestamp);
                builder.setShowWhen(true);
                
                if ("countdown".equals(mode) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    builder.setChronometerCountDown(true);
                }
            }
        }

        // Update progress bar if present
        JSObject progress = call.getObject("progress");
        boolean hasProgress = false;
        if (progress != null) {
            hasProgress = true;
            int max = progress.getInteger("max", 100);
            int current = progress.getInteger("current", 0);
            boolean indeterminate = progress.getBoolean("indeterminate", false);
            builder.setProgress(max, current, indeterminate);
        }

        // Use BigTextStyle to ensure content is visible
        if (config.timer != null || hasProgress) {
            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle()
                .bigText(finalMessage)
                .setBigContentTitle(finalTitle);
            builder.setStyle(bigTextStyle);
        }

        // Restore action buttons from config
        if (config.actionTypeId != null) {
            addActionsToLiveActivity(builder, id, config.notificationId, config.actionTypeId);
        }

        // Restore content intent
        addContentIntentToLiveActivity(builder, id, config.notificationId);

        // Silent update
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            builder.setSilent(true);
        }

        notificationManager.notify(config.notificationId, builder.build());
        call.resolve();
    }

    /**
     * End/dismiss a Live Activity.
     */
    @PluginMethod
    public void endLiveActivity(PluginCall call) {
        String id = call.getString("id");
        if (id == null) {
            call.reject("id is required");
            return;
        }

        LiveActivityConfig config = activeLiveActivities.get(id);
        if (config != null) {
            // Stop the progress service if running
            if (config.hasProgressService) {
                TimerProgressService.stopTimer(getContext());
            }
            
            // Cancel the notification
            notificationManager.cancel(config.notificationId);
            
            // Cancel any pending alarm
            cancelTimerEndAlarm(id);
            
            // Remove from tracking
            activeLiveActivities.remove(id);
        }

        call.resolve();
    }

    /**
     * Get list of active Live Activities.
     */
    @PluginMethod
    public void getActiveLiveActivities(PluginCall call) {
        JSObject result = new JSObject();
        JSArray activities = new JSArray();
        
        for (String activityId : activeLiveActivities.keySet()) {
            activities.put(activityId);
        }
        
        result.put("activities", activities);
        call.resolve(result);
    }

    /**
     * Create the Live Activity notification channel if it doesn't exist.
     */
    private void createLiveActivityChannelIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            android.app.NotificationChannel channel = notificationManager.getNotificationChannel(LIVE_ACTIVITY_CHANNEL_ID);
            if (channel == null) {
                channel = new android.app.NotificationChannel(
                    LIVE_ACTIVITY_CHANNEL_ID,
                    "Live Activities",
                    NotificationManager.IMPORTANCE_LOW // Low importance = no sound
                );
                channel.setDescription("Notifications for active timers and live activities");
                channel.setSound(null, null); // No sound
                channel.enableVibration(false);
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    /**
     * Schedule an alarm for when the timer ends.
     */
    private void scheduleTimerEndAlarm(String activityId, long targetTime) {
        Intent intent = new Intent(getContext(), TimerEndReceiver.class);
        intent.putExtra(TimerEndReceiver.ACTIVITY_ID_KEY, activityId);

        int requestCode = Math.abs(activityId.hashCode());
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            flags = flags | PendingIntent.FLAG_IMMUTABLE;
        }

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
            getContext(), requestCode, intent, flags
        );

        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                // Fallback to inexact alarm if exact alarms not allowed
                alarmManager.set(AlarmManager.RTC_WAKEUP, targetTime, pendingIntent);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, targetTime, pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, targetTime, pendingIntent);
            }
        }
    }

    /**
     * Cancel a pending timer end alarm.
     */
    private void cancelTimerEndAlarm(String activityId) {
        Intent intent = new Intent(getContext(), TimerEndReceiver.class);
        int requestCode = Math.abs(activityId.hashCode());
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            flags = flags | PendingIntent.FLAG_IMMUTABLE;
        }

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
            getContext(), requestCode, intent, flags
        );

        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null && pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}
