package com.capacitorjs.plugins.browser;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;

public class BrowserControllerActivity extends Activity {

    private boolean isCustomTabsOpen = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isCustomTabsOpen = false;

        if (BrowserPlugin.browserControllerListener != null) {
            BrowserPlugin.browserControllerListener.onControllerReady(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra("close")) {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isCustomTabsOpen) {
            isCustomTabsOpen = false;
            finish();
        } else {
            isCustomTabsOpen = true;
        }
    }

    public void open(Browser implementation, Uri url, Integer toolbarColor) {
        implementation.open(url, toolbarColor);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isCustomTabsOpen = false;
        BrowserPlugin.setBrowserControllerListener(null);
    }
}
