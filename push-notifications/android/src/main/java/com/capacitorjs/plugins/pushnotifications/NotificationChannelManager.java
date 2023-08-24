package com.capacitorjs.plugins.pushnotifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import androidx.core.app.NotificationCompat;
import com.getcapacitor.*;
import com.getcapacitor.util.WebColor;
import java.util.Arrays;
import java.util.List;

public class NotificationChannelManager {

    public static final String FOREGROUND_NOTIFICATION_CHANNEL_ID = "PushDefaultForeground";

    private Context context;
    private NotificationManager notificationManager;
    private PluginConfig config;

    public NotificationChannelManager(Context context, NotificationManager manager, PluginConfig config) {
        this.context = context;
        this.notificationManager = manager;
        this.config = config;
        createForegroundNotificationChannel();
    }

    private static String CHANNEL_ID = "id";
    private static String CHANNEL_NAME = "name";
    private static String CHANNEL_DESCRIPTION = "description";
    private static String CHANNEL_IMPORTANCE = "importance";
    private static String CHANNEL_VISIBILITY = "visibility";
    private static String CHANNEL_SOUND = "sound";
    private static String CHANNEL_VIBRATE = "vibration";
    private static String CHANNEL_USE_LIGHTS = "lights";
    private static String CHANNEL_LIGHT_COLOR = "lightColor";

    public void createChannel(PluginCall call) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            JSObject channel = new JSObject();
            if (call.getString(CHANNEL_ID) != null) {
                channel.put(CHANNEL_ID, call.getString(CHANNEL_ID));
            } else {
                call.reject("Channel missing identifier");
                return;
            }
            if (call.getString(CHANNEL_NAME) != null) {
                channel.put(CHANNEL_NAME, call.getString(CHANNEL_NAME));
            } else {
                call.reject("Channel missing name");
                return;
            }

            channel.put(CHANNEL_IMPORTANCE, call.getInt(CHANNEL_IMPORTANCE, NotificationManager.IMPORTANCE_DEFAULT));
            channel.put(CHANNEL_DESCRIPTION, call.getString(CHANNEL_DESCRIPTION, ""));
            channel.put(CHANNEL_VISIBILITY, call.getInt(CHANNEL_VISIBILITY, NotificationCompat.VISIBILITY_PUBLIC));
            channel.put(CHANNEL_SOUND, call.getString(CHANNEL_SOUND, null));
            channel.put(CHANNEL_VIBRATE, call.getBoolean(CHANNEL_VIBRATE, false));
            channel.put(CHANNEL_USE_LIGHTS, call.getBoolean(CHANNEL_USE_LIGHTS, false));
            channel.put(CHANNEL_LIGHT_COLOR, call.getString(CHANNEL_LIGHT_COLOR, null));
            createChannel(channel);
            call.resolve();
        } else {
            call.unavailable();
        }
    }

    public void createChannel(JSObject channel) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                channel.getString(CHANNEL_ID),
                channel.getString(CHANNEL_NAME),
                channel.getInteger(CHANNEL_IMPORTANCE)
            );
            notificationChannel.setDescription(channel.getString(CHANNEL_DESCRIPTION));
            notificationChannel.setLockscreenVisibility(channel.getInteger(CHANNEL_VISIBILITY));
            notificationChannel.enableVibration(channel.getBool(CHANNEL_VIBRATE));
            notificationChannel.enableLights(channel.getBool(CHANNEL_USE_LIGHTS));
            String lightColor = channel.getString(CHANNEL_LIGHT_COLOR);
            if (lightColor != null) {
                try {
                    notificationChannel.setLightColor(WebColor.parseColor(lightColor));
                } catch (IllegalArgumentException ex) {
                    Logger.error(Logger.tags("NotificationChannel"), "Invalid color provided for light color.", null);
                }
            }
            String sound = channel.getString(CHANNEL_SOUND, null);
            if (sound != null && !sound.isEmpty()) {
                if (sound.contains(".")) {
                    sound = sound.substring(0, sound.lastIndexOf('.'));
                }
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
                Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/raw/" + sound);
                notificationChannel.setSound(soundUri, audioAttributes);
            }
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    public void deleteChannel(PluginCall call) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String channelId = call.getString("id");
            notificationManager.deleteNotificationChannel(channelId);
            call.resolve();
        } else {
            call.unavailable();
        }
    }

    public void listChannels(PluginCall call) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            List<NotificationChannel> notificationChannels = notificationManager.getNotificationChannels();
            JSArray channels = new JSArray();
            for (NotificationChannel notificationChannel : notificationChannels) {
                JSObject channel = new JSObject();
                channel.put(CHANNEL_ID, notificationChannel.getId());
                channel.put(CHANNEL_NAME, notificationChannel.getName());
                channel.put(CHANNEL_DESCRIPTION, notificationChannel.getDescription());
                channel.put(CHANNEL_IMPORTANCE, notificationChannel.getImportance());
                channel.put(CHANNEL_VISIBILITY, notificationChannel.getLockscreenVisibility());
                channel.put(CHANNEL_SOUND, notificationChannel.getSound());
                channel.put(CHANNEL_VIBRATE, notificationChannel.shouldVibrate());
                channel.put(CHANNEL_USE_LIGHTS, notificationChannel.shouldShowLights());
                channel.put(CHANNEL_LIGHT_COLOR, String.format("#%06X", (0xFFFFFF & notificationChannel.getLightColor())));
                Logger.debug(Logger.tags("NotificationChannel"), "visibility " + notificationChannel.getLockscreenVisibility());
                Logger.debug(Logger.tags("NotificationChannel"), "importance " + notificationChannel.getImportance());
                channels.put(channel);
            }
            JSObject result = new JSObject();
            result.put("channels", channels);
            call.resolve(result);
        } else {
            call.unavailable();
        }
    }

    /**
     * Create notification channel
     */
    public void createForegroundNotificationChannel() {
        // Create the NotificationChannel only if presentationOptions is defined
        // Because the channel can't be changed after creation
        String[] presentation = config.getArray("presentationOptions");
        if (presentation != null) {
            // And only on API 26+ because the NotificationChannel class
            // is new and not in the support library
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = "Push Notifications Foreground";
                String description = "Push notifications in foreground";
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel channel = new NotificationChannel(FOREGROUND_NOTIFICATION_CHANNEL_ID, name, importance);
                channel.setDescription(description);
                if (Arrays.asList(presentation).contains("sound")) {
                    AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build();
                    channel.setSound(Settings.System.DEFAULT_NOTIFICATION_URI, audioAttributes);
                }
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                android.app.NotificationManager notificationManager = context.getSystemService(android.app.NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
