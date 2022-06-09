package com.capacitorjs.plugins.preferences;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.json.JSONException;

@CapacitorPlugin(name = "Preferences")
public class PreferencesPlugin extends Plugin {

    private Preferences preferences;

    @Override
    public void load() {
        preferences = new Preferences(getContext(), PreferencesConfiguration.DEFAULTS);
    }

    @PluginMethod
    public void configure(PluginCall call) {
        try {
            PreferencesConfiguration configuration = PreferencesConfiguration.DEFAULTS.clone();
            configuration.group = call.getString("group", PreferencesConfiguration.DEFAULTS.group);

            preferences = new Preferences(getContext(), configuration);
        } catch (CloneNotSupportedException e) {
            call.reject("Error while configuring", e);
            return;
        }
        call.resolve();
    }

    @PluginMethod
    public void get(PluginCall call) {
        String key = call.getString("key");
        if (key == null) {
            call.reject("Must provide key");
            return;
        }

        String value = preferences.get(key);

        JSObject ret = new JSObject();
        ret.put("value", value == null ? JSObject.NULL : value);
        call.resolve(ret);
    }

    @PluginMethod
    public void set(PluginCall call) {
        String key = call.getString("key");
        if (key == null) {
            call.reject("Must provide key");
            return;
        }

        String value = call.getString("value");
        preferences.set(key, value);

        call.resolve();
    }

    @PluginMethod
    public void remove(PluginCall call) {
        String key = call.getString("key");
        if (key == null) {
            call.reject("Must provide key");
            return;
        }

        preferences.remove(key);

        call.resolve();
    }

    @PluginMethod
    public void keys(PluginCall call) {
        Set<String> keySet = preferences.keys();
        String[] keys = keySet.toArray(new String[0]);

        JSObject ret = new JSObject();
        try {
            ret.put("keys", new JSArray(keys));
        } catch (JSONException ex) {
            call.reject("Unable to serialize response.", ex);
            return;
        }
        call.resolve(ret);
    }

    @PluginMethod
    public void clear(PluginCall call) {
        preferences.clear();
        call.resolve();
    }

    @PluginMethod
    public void migrate(PluginCall call) {
        List<String> migrated = new ArrayList<>();
        List<String> existing = new ArrayList<>();
        Preferences oldPreferences = new Preferences(getContext(), PreferencesConfiguration.DEFAULTS);

        for (String key : oldPreferences.keys()) {
            String value = oldPreferences.get(key);
            String currentValue = preferences.get(key);

            if (currentValue == null) {
                preferences.set(key, value);
                migrated.add(key);
            } else {
                existing.add(key);
            }
        }

        JSObject ret = new JSObject();
        ret.put("migrated", new JSArray(migrated));
        ret.put("existing", new JSArray(existing));
        call.resolve(ret);
    }

    @PluginMethod
    public void removeOld(PluginCall call) {
        call.resolve();
    }
}
