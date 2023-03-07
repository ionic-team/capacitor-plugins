package com.capacitorjs.plugins.screenreader;

import android.content.Context;
import android.media.AudioAttributes;
import android.speech.tts.TextToSpeech;
import android.view.accessibility.AccessibilityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ScreenReader {

    private Context context;
    private AccessibilityManager accessibilityManager;
    private List<ScreenReaderStateChangeListener> stateChangeListeners = new ArrayList<>();
    private TextToSpeech textToSpeech;

    public interface ScreenReaderStateChangeListener {
        void onScreenReaderStateChanged(boolean enabled);
    }

    ScreenReader(Context context) {
        this.context = context;
        this.accessibilityManager = (AccessibilityManager) this.context.getSystemService(Context.ACCESSIBILITY_SERVICE);
    }

    public void addStateChangeListener(ScreenReaderStateChangeListener listener) {
        stateChangeListeners.add(listener);
        accessibilityManager.addTouchExplorationStateChangeListener(listener::onScreenReaderStateChanged);
    }

    public void removeAllListeners() {
        for (ScreenReaderStateChangeListener listener : stateChangeListeners) {
            accessibilityManager.removeTouchExplorationStateChangeListener(listener::onScreenReaderStateChanged);
        }
    }

    public boolean isEnabled() {
        return accessibilityManager.isTouchExplorationEnabled();
    }

    public void speak(String text) {
        speak(text, "en");
    }

    public void speak(final String text, final String languageTag) {
        if (isEnabled()) {
            final Locale locale = Locale.forLanguageTag(languageTag);

            textToSpeech =
                new TextToSpeech(
                    context,
                    status -> {
                        AudioAttributes attributes = new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
                            .build();
                        textToSpeech.setAudioAttributes(attributes);
                        textToSpeech.setLanguage(locale);
                        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "capacitor-screen-reader" + System.currentTimeMillis());
                    }
                );
        }
    }
}
