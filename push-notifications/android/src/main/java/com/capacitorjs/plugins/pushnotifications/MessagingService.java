package com.capacitorjs.plugins.pushnotifications;

import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        PushNotificationsPlugin.sendRemoteMessage(remoteMessage);

        Intent broadcastIntent = new Intent("android.intent.action.capacitor.pushnotification");
        broadcastIntent.putExtra("remoteMessage", remoteMessage);
        sendBroadcast(broadcastIntent);
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        PushNotificationsPlugin.onNewToken(s);
    }
}
