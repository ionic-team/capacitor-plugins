package com.capacitorjs.plugins.applauncher;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import com.getcapacitor.JSObject;
import com.getcapacitor.Logger;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.util.InternalUtils;

@CapacitorPlugin(name = "AppLauncher")
public class AppLauncherPlugin extends Plugin {

    @PluginMethod
    public void canOpenUrl(PluginCall call) {
        String url = call.getString("url");
        if (url == null) {
            call.reject("Must supply a url");
            return;
        }

        Context ctx = this.getActivity().getApplicationContext();
        final PackageManager pm = ctx.getPackageManager();

        JSObject ret = new JSObject();
        try {
            InternalUtils.getPackageInfo(pm, url, PackageManager.GET_ACTIVITIES);
            ret.put("value", true);
            call.resolve(ret);
            return;
        } catch (PackageManager.NameNotFoundException e) {
            Logger.error(getLogTag(), "Package name '" + url + "' not found!", null);
        }
        if (!canResolve(pm, new Intent(Intent.ACTION_VIEW, Uri.parse(url)))) {
            ret.put("value", canResolve(pm, new Intent(url)));
        } else {
            ret.put("value", true);
        }
        call.resolve(ret);
    }

    private boolean canResolve(PackageManager pm, Intent intent) {
        ResolveInfo resolve = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return resolve != null;
    }

    @PluginMethod
    public void openUrl(PluginCall call) {
        String url = call.getString("url");
        if (url == null) {
            call.reject("Must provide a url to open");
            return;
        }
        JSObject ret = new JSObject();
        final PackageManager manager = getContext().getPackageManager();
        Intent launchIntent = new Intent(Intent.ACTION_VIEW);
        launchIntent.setData(Uri.parse(url));
        if (!canLaunchIntent(launchIntent)) {
            if (!canLaunchIntent(manager.getLaunchIntentForPackage(url))) {
                ret.put("completed", canLaunchIntent(new Intent(url)));
            } else {
                ret.put("completed", true);
            }
        } else {
            ret.put("completed", true);
        }
        call.resolve(ret);
    }

    private boolean canLaunchIntent(Intent intent) {
        try {
            getActivity().startActivity(intent);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
