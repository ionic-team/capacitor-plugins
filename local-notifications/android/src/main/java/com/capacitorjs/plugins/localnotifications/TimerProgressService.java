package com.capacitorjs.plugins.localnotifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import com.getcapacitor.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Foreground Service that handles timer progress updates for Live Activities.
 * This service continues to run even when the app is closed, ensuring that:
 * 1. Progress bar updates every 2 seconds
 * 2. Alert state is triggered when max duration is exceeded
 * 3. Vibration is triggered on alert state
 *
 * @since 7.1.0
 */
public class TimerProgressService extends Service {

    private static final String TAG = "TimerProgressService";
    private static final String PREFS_NAME = "timer_progress_prefs";
    private static final long UPDATE_INTERVAL_MS = 2000; // Update every 2 seconds

    // Intent action keys
    public static final String ACTION_START_TIMER = "com.capacitorjs.localnotifications.START_TIMER";
    public static final String ACTION_STOP_TIMER = "com.capacitorjs.localnotifications.STOP_TIMER";
    
    // Intent extra keys
    public static final String EXTRA_ACTIVITY_ID = "activityId";
    public static final String EXTRA_NOTIFICATION_ID = "notificationId";
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_MESSAGE = "message";
    public static final String EXTRA_CHANNEL_ID = "channelId";
    public static final String EXTRA_ACTION_TYPE_ID = "actionTypeId";
    public static final String EXTRA_START_TIMESTAMP = "startTimestamp";
    public static final String EXTRA_MAX_DURATION_MS = "maxDurationMs";

    private Handler handler;
    private Runnable updateRunnable;
    private NotificationManager notificationManager;
    private NotificationStorage notificationStorage;

    // Current timer state
    private String currentActivityId;
    private int currentNotificationId;
    private String currentTitle;
    private String currentMessage;
    private String currentChannelId;
    private String currentActionTypeId;
    private long startTimestamp;
    private long maxDurationMs;
    private boolean hasExceeded = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.debug(Logger.tags("LN"), TAG + ": Service created");
        handler = new Handler(Looper.getMainLooper());
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationStorage = new NotificationStorage(this);
        // Note: channel is created in createInitialNotification() when we have the channelId
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            Logger.debug(Logger.tags("LN"), TAG + ": Intent is null, stopping service");
            stopSelf();
            return START_NOT_STICKY;
        }

        String action = intent.getAction();
        
        if (ACTION_STOP_TIMER.equals(action)) {
            Logger.debug(Logger.tags("LN"), TAG + ": Stopping timer");
            stopTimer();
            stopSelf();
            return START_NOT_STICKY;
        }

        if (ACTION_START_TIMER.equals(action)) {
            // Extract timer configuration from intent
            currentActivityId = intent.getStringExtra(EXTRA_ACTIVITY_ID);
            currentNotificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 0);
            currentTitle = intent.getStringExtra(EXTRA_TITLE);
            currentMessage = intent.getStringExtra(EXTRA_MESSAGE);
            currentChannelId = intent.getStringExtra(EXTRA_CHANNEL_ID);
            currentActionTypeId = intent.getStringExtra(EXTRA_ACTION_TYPE_ID);
            startTimestamp = intent.getLongExtra(EXTRA_START_TIMESTAMP, System.currentTimeMillis());
            maxDurationMs = intent.getLongExtra(EXTRA_MAX_DURATION_MS, 0);
            hasExceeded = false;

            Logger.debug(Logger.tags("LN"), TAG + ": Starting timer for activity " + currentActivityId + 
                ", maxDuration=" + maxDurationMs + "ms");

            // Save to SharedPreferences for persistence
            saveTimerConfig();

            // Start foreground service using the main timer notification
            // This avoids having two separate notifications
            startForeground(currentNotificationId, createInitialNotification());

            // Start the update loop
            startUpdateLoop();
        }

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Logger.debug(Logger.tags("LN"), TAG + ": Service destroyed");
        stopTimer();
        super.onDestroy();
    }

    private static final String DEFAULT_CHANNEL_ID = "live_activity";
    
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = currentChannelId != null ? currentChannelId : DEFAULT_CHANNEL_ID;
            NotificationChannel channel = notificationManager.getNotificationChannel(channelId);
            if (channel == null) {
                channel = new NotificationChannel(
                    channelId,
                    "Live Activities",
                    NotificationManager.IMPORTANCE_HIGH
                );
                channel.setDescription("Timer notifications");
                channel.enableVibration(true);
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private Notification createInitialNotification() {
        // Ensure the channel exists
        createNotificationChannel();
        
        String channelId = currentChannelId != null ? currentChannelId : DEFAULT_CHANNEL_ID;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
            .setContentTitle(currentTitle)
            .setContentText(currentMessage != null ? currentMessage : "")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .setAutoCancel(false)
            .setUsesChronometer(true)
            .setWhen(startTimestamp)
            .setShowWhen(true)
            .setProgress(100, 0, false)
            // Vibrer au demarrage
            .setVibrate(new long[]{0, 300, 200, 300})
            .setDefaults(NotificationCompat.DEFAULT_LIGHTS);

        // Use BigTextStyle
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle()
            .bigText(currentMessage != null ? currentMessage : "")
            .setBigContentTitle(currentTitle);
        builder.setStyle(bigTextStyle);

        // Add action buttons
        if (currentActionTypeId != null) {
            addActionsToNotification(builder);
        }

        // Add content intent
        addContentIntent(builder);

        return builder.build();
    }

    private void saveTimerConfig() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit()
            .putString("activityId", currentActivityId)
            .putInt("notificationId", currentNotificationId)
            .putString("title", currentTitle)
            .putString("message", currentMessage)
            .putString("channelId", currentChannelId)
            .putString("actionTypeId", currentActionTypeId)
            .putLong("startTimestamp", startTimestamp)
            .putLong("maxDurationMs", maxDurationMs)
            .putBoolean("hasExceeded", hasExceeded)
            .apply();
    }

    private void clearTimerConfig() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().clear().apply();
    }

    private void startUpdateLoop() {
        if (updateRunnable != null) {
            handler.removeCallbacks(updateRunnable);
        }

        updateRunnable = new Runnable() {
            @Override
            public void run() {
                updateProgress();
                // Continue updating as long as service is running
                handler.postDelayed(this, UPDATE_INTERVAL_MS);
            }
        };

        // Start immediately
        handler.post(updateRunnable);
    }

    private void stopTimer() {
        if (updateRunnable != null) {
            handler.removeCallbacks(updateRunnable);
            updateRunnable = null;
        }
        clearTimerConfig();
    }

    private void updateProgress() {
        if (currentActivityId == null || maxDurationMs <= 0) {
            return;
        }

        long now = System.currentTimeMillis();
        long elapsedMs = now - startTimestamp;
        float progressPercent = Math.min((float) elapsedMs / maxDurationMs * 100, 100);
        boolean isExceeded = elapsedMs >= maxDurationMs;

        // Check if we just exceeded
        if (isExceeded && !hasExceeded) {
            hasExceeded = true;
            saveTimerConfig();
            Logger.debug(Logger.tags("LN"), TAG + ": Timer exceeded! Updating to alert state");
            updateNotificationToAlert();
            
            // Notify JavaScript layer
            LocalNotificationsPlugin plugin = LocalNotificationsPlugin.getLocalNotificationsInstance();
            if (plugin != null) {
                plugin.fireTimerEnded(currentActivityId);
            }
            return;
        }

        // If already exceeded, don't update progress anymore
        if (hasExceeded) {
            return;
        }

        // Update the notification with new progress
        updateNotificationProgress((int) progressPercent);
    }

    private void updateNotificationProgress(int progressPercent) {
        String channelId = currentChannelId != null ? currentChannelId : DEFAULT_CHANNEL_ID;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
            .setContentTitle(currentTitle)
            .setContentText(currentMessage != null ? currentMessage : "")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setOngoing(true)
            .setAutoCancel(false)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOnlyAlertOnce(true) // Don't vibrate on updates
            .setUsesChronometer(true)
            .setWhen(startTimestamp)
            .setShowWhen(true)
            .setProgress(100, progressPercent, false);

        // Use BigTextStyle
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle()
            .bigText(currentMessage != null ? currentMessage : "")
            .setBigContentTitle(currentTitle);
        builder.setStyle(bigTextStyle);

        // Add action buttons
        if (currentActionTypeId != null) {
            addActionsToNotification(builder);
        }

        // Add content intent
        addContentIntent(builder);

        notificationManager.notify(currentNotificationId, builder.build());
    }

    private void updateNotificationToAlert() {
        String alertTitle = "⚠️ DURÉE DÉPASSÉE";
        String alertMessage = "Temps de stationnement dépassé";

        String channelId = currentChannelId != null ? currentChannelId : DEFAULT_CHANNEL_ID;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
            .setContentTitle(alertTitle)
            .setContentText(alertMessage)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setOngoing(true)
            .setAutoCancel(false)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setUsesChronometer(true)
            .setWhen(startTimestamp)
            .setShowWhen(true)
            .setProgress(100, 100, false)
            // Important: NE PAS utiliser setOnlyAlertOnce pour permettre la vibration
            // Vibration pour l'alerte
            .setVibrate(new long[]{0, 500, 250, 500, 250, 500})
            .setDefaults(NotificationCompat.DEFAULT_VIBRATE | NotificationCompat.DEFAULT_LIGHTS | NotificationCompat.DEFAULT_SOUND);

        // Use BigTextStyle with alert styling
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle()
            .bigText(alertMessage)
            .setBigContentTitle(alertTitle);
        builder.setStyle(bigTextStyle);

        // Add action buttons
        if (currentActionTypeId != null) {
            addActionsToNotification(builder);
        }

        // Add content intent
        addContentIntent(builder);

        notificationManager.notify(currentNotificationId, builder.build());
    }

    private void addActionsToNotification(NotificationCompat.Builder builder) {
        NotificationAction[] actionGroup = notificationStorage.getActionGroup(currentActionTypeId);
        if (actionGroup == null) {
            return;
        }

        int flags = PendingIntent.FLAG_CANCEL_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            flags = flags | PendingIntent.FLAG_MUTABLE;
        }

        for (NotificationAction action : actionGroup) {
            Intent actionIntent = buildActionIntent(action.getId());
            PendingIntent actionPendingIntent = PendingIntent.getActivity(
                this,
                currentNotificationId + action.getId().hashCode(),
                actionIntent,
                flags
            );

            NotificationCompat.Action.Builder actionBuilder = new NotificationCompat.Action.Builder(
                android.R.drawable.ic_menu_send,
                action.getTitle(),
                actionPendingIntent
            );

            builder.addAction(actionBuilder.build());
        }
    }

    private Intent buildActionIntent(String actionId) {
        String packageName = getPackageName();
        Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent == null) {
            intent = new Intent();
            intent.setPackage(packageName);
        }
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        // FLAG_ACTIVITY_NEW_TASK is required when starting from a service
        // FLAG_ACTIVITY_SINGLE_TOP prevents creating a new activity if one exists
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(LocalNotificationManager.NOTIFICATION_INTENT_KEY, currentNotificationId);
        intent.putExtra(LocalNotificationManager.ACTION_INTENT_KEY, actionId);
        intent.putExtra("liveActivityId", currentActivityId);
        intent.putExtra(LocalNotificationManager.NOTIFICATION_IS_REMOVABLE_KEY, true);
        
        // Build notification JSON for action handler
        JSONObject notificationObj = new JSONObject();
        try {
            notificationObj.put("id", currentNotificationId);
            notificationObj.put("liveActivityId", currentActivityId);
            notificationObj.put("actionId", actionId);
        } catch (JSONException e) {
            Logger.error(Logger.tags("LN"), "Error building notification JSON", e);
        }
        intent.putExtra(LocalNotificationManager.NOTIFICATION_OBJ_INTENT_KEY, notificationObj.toString());
        
        return intent;
    }

    private void addContentIntent(NotificationCompat.Builder builder) {
        Intent intent = buildActionIntent("tap");
        int flags = PendingIntent.FLAG_CANCEL_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            flags = flags | PendingIntent.FLAG_MUTABLE;
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, currentNotificationId, intent, flags);
        builder.setContentIntent(pendingIntent);
    }

    /**
     * Static method to start the timer progress service.
     */
    public static void startTimer(Context context, String activityId, int notificationId,
                                   String title, String message, String channelId, String actionTypeId,
                                   long startTimestamp, long maxDurationMs) {
        Intent intent = new Intent(context, TimerProgressService.class);
        intent.setAction(ACTION_START_TIMER);
        intent.putExtra(EXTRA_ACTIVITY_ID, activityId);
        intent.putExtra(EXTRA_NOTIFICATION_ID, notificationId);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_MESSAGE, message);
        intent.putExtra(EXTRA_CHANNEL_ID, channelId);
        intent.putExtra(EXTRA_ACTION_TYPE_ID, actionTypeId);
        intent.putExtra(EXTRA_START_TIMESTAMP, startTimestamp);
        intent.putExtra(EXTRA_MAX_DURATION_MS, maxDurationMs);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    /**
     * Static method to stop the timer progress service.
     */
    public static void stopTimer(Context context) {
        Intent intent = new Intent(context, TimerProgressService.class);
        intent.setAction(ACTION_STOP_TIMER);
        context.startService(intent);
    }
}
