package com.capacitorjs.plugins.screenreader;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.view.accessibility.AccessibilityManager;
import com.getcapacitor.JSObject;
import java.util.Locale;

public class ScreenReader {
    private Context context;
    private AccessibilityManager am;
    private TextToSpeech tts;

    public interface ScreenReaderStateChangeListener {
        void onScreenReaderStateChanged(boolean enabled);
    }

    ScreenReader(Context context) {
        this.context = context;
        this.am = (AccessibilityManager) this.context.getSystemService(Context.ACCESSIBILITY_SERVICE);
    }

    public void addStateChangeListener(ScreenReaderStateChangeListener listener) {
        am.addTouchExplorationStateChangeListener(listener::onScreenReaderStateChanged);
    }

    public boolean isEnabled() {
        return am.isTouchExplorationEnabled();
    }

    public void speak(String text) {
        speak(text, "en");
    }

    public void speak(final String text, final String languageTag) {
        if (isEnabled()) {
            final Locale locale = Locale.forLanguageTag(languageTag);

            tts =
                new TextToSpeech(
                    context,
                    status -> {
                        tts.setLanguage(locale);
                        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "capacitor-screen-reader" + System.currentTimeMillis());
                    }
                );
        }
    }
}
