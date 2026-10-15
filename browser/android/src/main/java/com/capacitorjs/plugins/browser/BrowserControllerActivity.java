package com.capacitorjs.plugins.browser;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;

public class BrowserControllerActivity extends ComponentActivity {

    private ActivityResultLauncher<Intent> customTabLauncher;
    private Browser implementation;
    private boolean isFirstResume = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // close() starts a new instance when the previous one was already cleared off the task
        // (e.g. by a deep link into a singleTask activity) but not destroyed yet. There is nothing
        // to close then, and the last open() must not run again.
        if (getIntent().hasExtra("close")) {
            finish();
            return;
        }

        customTabLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), (result) -> {
            if (implementation != null) {
                implementation.notifyBrowserFinished();
            }
            finish();
        });

        // Each open() is served once, by the controller it started.
        BrowserControllerListener listener = BrowserPlugin.browserControllerListener;
        BrowserPlugin.browserControllerListener = null;
        if (listener != null) {
            listener.onControllerReady(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // On Android <14 the ActivityResult callback is not reliably delivered
        // when the Custom Tab is dismissed from PiP, leaving this launcher
        // stranded on top of the task and freezing the app UI. Finishing on
        // the second resume (fires when control returns from the Custom Tab) releases
        // the task. Skipped on 14+ where the ActivityResult callback handles it.
        if (isFirstResume) {
            isFirstResume = false;
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            finish();
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
        implementation = null;
        BrowserPlugin.onControllerDestroyed(this);
    }
}
