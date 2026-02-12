package com.capacitorjs.plugins.localnotifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.getcapacitor.Logger;

/**
 * BroadcastReceiver that handles timer end events for Live Activities.
 * When a timer countdown reaches zero, this receiver is triggered by AlarmManager
 * and notifies the JavaScript layer via the "liveActivityEnded" event.
 *
 * @since 7.1.0
 */
public class TimerEndReceiver extends BroadcastReceiver {

    public static final String ACTIVITY_ID_KEY = "activityId";

    @Override
    public void onReceive(Context context, Intent intent) {
        String activityId = intent.getStringExtra(ACTIVITY_ID_KEY);
        
        if (activityId == null) {
            Logger.debug(Logger.tags("LN"), "TimerEndReceiver: No activity ID provided");
            return;
        }
        
        Logger.debug(Logger.tags("LN"), "TimerEndReceiver: Timer ended for activity " + activityId);
        
        // Notify the JavaScript layer that timer ended
        // Use the public method fireTimerEnded since notifyListeners is protected
        LocalNotificationsPlugin plugin = LocalNotificationsPlugin.getLocalNotificationsInstance();
        if (plugin != null) {
            plugin.fireTimerEnded(activityId);
        } else {
            Logger.debug(Logger.tags("LN"), "TimerEndReceiver: Plugin instance not available");
        }
    }
}
