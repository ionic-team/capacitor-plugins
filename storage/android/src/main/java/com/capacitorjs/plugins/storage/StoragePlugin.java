package com.capacitorjs.plugins.storage;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import java.util.Set;
import org.json.JSONException;

@NativePlugin(name = "Storage")
public class StoragePlugin extends Plugin {
    private Storage storage;

    @Override
    public void load() {
        storage = new Storage(getContext());
    }

    @PluginMethod
    public void get(PluginCall call) {
        String key = call.getString("key");
        if (key == null) {
            call.reject("Must provide key");
            return;
        }

        String value = storage.get(key);

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
        storage.set(key, value);

        call.resolve();
    }

    @PluginMethod
    public void remove(PluginCall call) {
        String key = call.getString("key");
        if (key == null) {
            call.reject("Must provide key");
            return;
        }

        storage.remove(key);

        call.resolve();
    }

    @PluginMethod
    public void keys(PluginCall call) {
        Set<String> keySet = storage.keys();
        String[] keys = keySet.toArray(new String[0]);

        JSObject ret = new JSObject();
        try {
            ret.put("keys", new JSArray(keys));
        } catch (JSONException ex) {
            call.reject("Unable to create key array.");
            return;
        }
        call.resolve(ret);
    }

    @PluginMethod
    public void clear(PluginCall call) {
        storage.clear();
        call.resolve();
    }
}
