package com.capacitorjs.plugins.pushnotifications;

import static org.junit.Assert.*;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SdkSuppress;
import androidx.test.platform.app.InstrumentationRegistry;
import com.getcapacitor.JSObject;
import com.getcapacitor.PluginCall;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class NotificationSettingsTest {

    private Context context;
    private NotificationManager notificationManager;
    private NotificationChannelManager channelManager;
    private final List<String> channelIds = new ArrayList<>();

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        channelManager = new NotificationChannelManager(context, notificationManager, null);
    }

    @After
    public void tearDown() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            for (String id : channelIds) {
                notificationManager.deleteNotificationChannel(id);
            }
        }
    }

    @Test
    public void checkAppEnabledUsesSystemSettingsWithoutRegistration() throws Exception {
        PushNotificationsPlugin plugin = new PushNotificationsPlugin() {
            @Override
            public Context getContext() {
                return context;
            }
        };
        RecordingCall call = new RecordingCall("checkAppEnabled", new JSObject());

        plugin.checkAppEnabled(call);

        assertNotNull(call.result);
        assertEquals(notificationManager.areNotificationsEnabled(), call.result.getBoolean("value"));
    }

    @Test
    @SdkSuppress(minSdkVersion = 26)
    public void channelChecksAndListingAgreeForEveryImportance() throws Exception {
        int[] importanceLevels = {
            NotificationManager.IMPORTANCE_NONE,
            NotificationManager.IMPORTANCE_MIN,
            NotificationManager.IMPORTANCE_LOW,
            NotificationManager.IMPORTANCE_DEFAULT,
            NotificationManager.IMPORTANCE_HIGH
        };
        for (int importance : importanceLevels) {
            String id = UUID.randomUUID().toString();
            channelIds.add(id);
            notificationManager.createNotificationChannel(new NotificationChannel(id, "Test channel", importance));
            RecordingCall check = new RecordingCall("checkChannelEnabled", new JSObject().put("id", id));
            channelManager.checkChannelEnabled(check);
            assertNotNull(check.result);
            assertEquals(importance != NotificationManager.IMPORTANCE_NONE, check.result.getBoolean("value"));
        }

        RecordingCall list = new RecordingCall("listChannels", new JSObject());
        channelManager.listChannels(list);
        assertNotNull(list.result);
        JSONArray channels = list.result.getJSONArray("channels");
        int found = 0;
        for (int index = 0; index < channels.length(); index++) {
            JSONObject channel = channels.getJSONObject(index);
            if (channelIds.contains(channel.getString("id"))) {
                assertEquals(channel.getInt("importance") != NotificationManager.IMPORTANCE_NONE, channel.getBoolean("enabled"));
                found++;
            }
        }
        assertEquals(importanceLevels.length, found);
    }

    @Test
    @SdkSuppress(minSdkVersion = 26)
    public void missingChannelIsDisabled() throws Exception {
        RecordingCall call = new RecordingCall("checkChannelEnabled", new JSObject().put("id", UUID.randomUUID().toString()));
        channelManager.checkChannelEnabled(call);
        assertNotNull(call.result);
        assertFalse(call.result.getBoolean("value"));
    }

    @Test
    @SdkSuppress(minSdkVersion = 26)
    public void missingOrEmptyIdentifierIsRejected() {
        for (JSObject options : new JSObject[] { new JSObject(), new JSObject().put("id", "") }) {
            RecordingCall call = new RecordingCall("checkChannelEnabled", options);
            channelManager.checkChannelEnabled(call);
            assertNull(call.result);
            assertEquals("Channel missing identifier", call.errorMessage);
        }
    }

    @Test
    @SdkSuppress(maxSdkVersion = 25)
    public void channelChecksAreUnavailableBeforeAndroidO() {
        RecordingCall call = new RecordingCall("checkChannelEnabled", new JSObject().put("id", "general"));
        channelManager.checkChannelEnabled(call);
        assertNull(call.result);
        assertEquals("UNAVAILABLE", call.errorCode);
    }

    private static class RecordingCall extends PluginCall {

        JSObject result;
        String errorMessage;
        String errorCode;

        RecordingCall(String method, JSObject options) {
            super(null, "PushNotifications", "test", method, options);
        }

        @Override
        public void resolve(JSObject data) {
            result = data;
        }

        @Override
        public void reject(String message, String code, Exception exception, JSObject data) {
            errorMessage = message;
            errorCode = code;
        }
    }
}
