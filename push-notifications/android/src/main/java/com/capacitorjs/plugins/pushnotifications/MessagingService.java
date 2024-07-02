package com.capacitorjs.plugins.pushnotifications;

import androidx.annotation.NonNull;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Handle badge count update
        if (remoteMessage.getData().size() > 0) {
            Map<String, String> data = remoteMessage.getData();
            if (data.containsKey("count")) {
                int badgeCount = Integer.parseInt(data.get("count"));
                updateBadgeCount(badgeCount);
            }
        }

        PushNotificationsPlugin.sendRemoteMessage(remoteMessage);
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        PushNotificationsPlugin.onNewToken(s);
    }

    private void updateBadgeCount(int badgeCount) {
        // Use ShortcutBadger to set the badge count
        ShortcutBadger.applyCount(this, badgeCount);
    }
}
