package com.capacitorjs.plugins.browser;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;

public class BrowserControllerActivity extends ComponentActivity {

    private ActivityResultLauncher<Intent> customTabLauncher;
    private Browser implementation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        customTabLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), (result) -> {
            if (implementation != null) {
                implementation.notifyBrowserFinished();
            }
            finish();
        });

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

    public void open(Browser implementation, Uri url, Integer toolbarColor) {
        this.implementation = implementation;
        implementation.setCustomTabLauncher(customTabLauncher);
        implementation.open(url, toolbarColor);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BrowserPlugin.setBrowserControllerListener(null);
    }
}
