package com.capacitorjs.plugins.browser;

import static androidx.browser.customtabs.CustomTabsIntent.SHARE_STATE_ON;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.*;

/**
 * The Browser class implements Custom Chrome Tabs. See
 * https://developer.chrome.com/multidevice/android/customtabs for background
 * on how this code works.
 */
public class Browser {

    /**
     * Interface for callbacks for browser events.
     */
    interface BrowserEventListener {
        void onBrowserEvent(int event);
    }

    /**
     * Sent when the browser has loaded the initial page.
     */
    public static final int BROWSER_LOADED = 1;
    /**
     * Sent when the browser is finished.
     */
    public static final int BROWSER_FINISHED = 2;

    // private properties
    @Nullable
    private BrowserEventListener browserEventListener;

    private Context context;
    private static final String FALLBACK_CUSTOM_TAB_PACKAGE_NAME = "com.android.chrome";
    private CustomTabsClient customTabsClient;
    private CustomTabsSession browserSession;
    private boolean isInitialLoad = false;
    private EventGroup group;
    private CustomTabsServiceConnection connection = new CustomTabsServiceConnection() {
        @Override
        public void onCustomTabsServiceConnected(ComponentName name, CustomTabsClient client) {
            customTabsClient = client;
            client.warmup(0);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {}
    };

    /**
     * Create network browser object.
     * @param context
     */
    public Browser(@NonNull Context context) {
        this.context = context;
        this.group = new EventGroup(this::handleGroupCompletion);
    }

    /**
     * Set the object to receive callbacks.
     * @param listener
     */
    public void setBrowserEventListener(@Nullable BrowserEventListener listener) {
        this.browserEventListener = listener;
    }

    /**
     * Return the object that is receiving callbacks.
     * @return listener
     */
    @Nullable
    public BrowserEventListener getBrowserEventListenerListener() {
        return browserEventListener;
    }

    /**
     * Open the browser to the specified URL.
     * @param url
     */
    public void open(Uri url) {
        open(url, null);
    }

    /**
     * Open the browser to the specified URL with the specified toolbar color.
     * @param url
     * @param toolbarColor
     */
    public void open(Uri url, @Nullable Integer toolbarColor) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder(getCustomTabsSession());

        builder.setShareState(SHARE_STATE_ON);

        if (toolbarColor != null) {
            CustomTabColorSchemeParams params = new CustomTabColorSchemeParams.Builder().setToolbarColor(toolbarColor.intValue()).build();
            builder.setDefaultColorSchemeParams(params);
        }

        CustomTabsIntent tabsIntent = builder.build();
        tabsIntent.intent.putExtra(Intent.EXTRA_REFERRER, Uri.parse(Intent.URI_ANDROID_APP_SCHEME + "//" + context.getPackageName()));

        isInitialLoad = true;
        group.reset();
        tabsIntent.launchUrl(context, url);
    }

    /**
     * Bind to the custom tabs service, required to be called in the `onResume` lifecycle event.
     */
    public boolean bindService() {
        String customTabPackageName = CustomTabsClient.getPackageName(context, null);
        if (null == customTabPackageName) {
            customTabPackageName = FALLBACK_CUSTOM_TAB_PACKAGE_NAME;
        }
        boolean result = CustomTabsClient.bindCustomTabsService(context, customTabPackageName, connection);
        group.leave();
        return result;
    }

    /**
     * Unbind the custom tabs service, required to be called in the `onPause` lifecycle event.
     */
    public void unbindService() {
        context.unbindService(connection);
        group.enter();
    }

    private void handledNavigationEvent(int navigationEvent) {
        switch (navigationEvent) {
            case CustomTabsCallback.NAVIGATION_FINISHED:
                if (isInitialLoad) {
                    if (browserEventListener != null) {
                        browserEventListener.onBrowserEvent(BROWSER_LOADED);
                    }
                    isInitialLoad = false;
                }
                break;
            case CustomTabsCallback.TAB_HIDDEN:
                group.leave();
                break;
            case CustomTabsCallback.TAB_SHOWN:
                group.enter();
                break;
        }
    }

    private void handleGroupCompletion() {
        // events such as TAB_HIDDEN and onPause can occur for multiple reasons and in
        // different sequences so there is no single point to fire this. so we rely on the
        // event group to track when it is safe to assume that the browser is done.
        if (browserEventListener != null) {
            browserEventListener.onBrowserEvent(BROWSER_FINISHED);
        }
    }

    @Nullable
    private CustomTabsSession getCustomTabsSession() {
        if (customTabsClient == null) {
            return null;
        }

        if (browserSession == null) {
            browserSession =
                customTabsClient.newSession(
                    new CustomTabsCallback() {
                        @Override
                        public void onNavigationEvent(int navigationEvent, Bundle extras) {
                            handledNavigationEvent(navigationEvent);
                        }
                    }
                );
        }

        return browserSession;
    }
}
