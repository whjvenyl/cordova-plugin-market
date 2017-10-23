package com.xmartlabs.cordova.market;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import java.util.List;

/**
 * Interact with Google Play.
 *
 * @author Miguel Revetria <miguel@xmartlabs.com>
 * @license Apache 2.0
 */
public class Market extends CordovaPlugin
{
    /**
     * Executes the request and returns PluginResult.
     *
     * @param action
     *          Action to perform.
     * @param args
     *          Arguments to the action.
     * @param callbackId
     *          JavaScript callback ID.
     * @return A PluginResult object with a status and message.
     */
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
        try {
            if (action.equals("open")) {
                if (args.length() == 1 || args.length() == 2) {
                    String appId = args.getString(0);
                    this.openGooglePlay(appId);
                    callbackContext.success();
                    return true;
                }
            } else if (action.equals("search")) {
                if (args.length() == 1) {
                    String key = args.getString(0);
                    this.searchGooglePlay(key);

                    callbackContext.success();
                    return true;
                }
            }
        } catch (JSONException e) {
            Log.d("CordovaLog","Plugin Market: cannot parse args.");
            e.printStackTrace();
        } catch (android.content.ActivityNotFoundException e) {
            Log.d("CordovaLog","Plugin Market: cannot open Google Play activity.");
            e.printStackTrace();
        }

        return false;
    }


    // From http://stackoverflow.com/questions/11753000/how-to-open-the-google-play-store-directly-from-my-android-application

    /**
     * Open the appId details on Google Play .
     *
     * @param appId
     *            Application Id on Google Play.
     *            E.g.: com.google.earth
     */

    public  void openGooglePlay(String appId) {
        Context context = this.cordova.getActivity().getApplicationContext();
        PackageManager manager = context.getPackageManager();

        Intent directIntent = manager.getLaunchIntentForPackage(appId);

        // app with url scheme is registered
        if (directIntent != null) {
          context.startActivity(directIntent);
        } else {
          Intent rateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appId));
          boolean marketFound = false;

          // find all applications able to handle our rateIntent
          final List<ResolveInfo> otherApps = manager.queryIntentActivities(rateIntent, 0);
          for (ResolveInfo otherApp: otherApps) {
              // look for Google Play application
              if (otherApp.activityInfo.applicationInfo.packageName.equals("com.android.vending")) {

                  ActivityInfo otherAppActivity = otherApp.activityInfo;
                  ComponentName componentName = new ComponentName(
                          otherAppActivity.applicationInfo.packageName,
                          otherAppActivity.name
                  );
                  // make sure it does NOT open in the stack of your activity
                  rateIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                  // task reparenting if needed
                  rateIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                  // if the Google Play was already open in a search result
                  //  this make sure it still go to the app page you requested
                  rateIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                  // this make sure only the Google Play app is allowed to
                  // intercept the intent
                  rateIntent.setComponent(componentName);
                  context.startActivity(rateIntent);
                  marketFound = true;
                  break;
              }
          }

          // if GP not present on device, open web browser
          if (!marketFound) {
              Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appId));
              context.startActivity(webIntent);
          }
        }
    }

    /**
     * search the details on Google Play .
     *
     * @param searchKeyword
     *            Application Id on Google Play.
     *            E.g.: earth
     */
    private void searchGooglePlay(String key) throws android.content.ActivityNotFoundException {
        Context context = this.cordova.getActivity().getApplicationContext();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=" + key));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
