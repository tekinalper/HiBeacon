package com.hibeacon;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alper Tekin - alpertekin.com on 17.2.2016.
 */
public class HiBeaconApp extends Application implements BootstrapNotifier {
    private static final String TAG = "HiBeaconApp";
    private RegionBootstrap regionBootstrap;
    //    private BackgroundPowerSaver backgroundPowerSaver;
    private static boolean activityVisible;

    private String jsonData = "[{\"text\":\"A cup of coffee?\",\"imageUrl\":\"https://s-media-cache-ak0.pinimg.com/564x/4d/ab/26/4dab26fe36fac02a02f0c4de1cf28fb3.jpg\"}," +
            "{\"text\":\"We offer chocolate cake along with coffee :)\",\"imageUrl\":\"https://s-media-cache-ak0.pinimg.com/564x/ed/88/13/ed8813d8e6bc675c5a74a90358adfe9b.jpg\"}]";
    public static ArrayList<Item> items;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "App started up");
        BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
        // To detect proprietary beacons, you must add a line like below corresponding to your beacon
        // type.  Do a web search for "setBeaconLayout" to get the proper expression.
        beaconManager.getBeaconParsers().clear();
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.setBackgroundScanPeriod(5000l);
        beaconManager.setBackgroundBetweenScanPeriod(0l);
        try {
            beaconManager.updateScanPeriods();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        // wake up the app when any beacon is seen (you can specify specific id filers in the parameters below)
        Region region = new Region("com.hibeacon.boostrapRegion", null, null, null);
        regionBootstrap = new RegionBootstrap(this, region);

        // simply constructing this class and holding a reference to it in your custom Application
        // class will automatically cause the BeaconLibrary to save battery whenever the application
        // is not visible.  This reduces bluetooth power usage by about 60%
        // backgroundPowerSaver = new BackgroundPowerSaver(this);


        Gson gson = new Gson();
        items = gson.fromJson(jsonData, new TypeToken<List<Item>>() {
        }.getType());
    }

    @Override
    public void didDetermineStateForRegion(int arg0, Region arg1) {
        // Don't care
    }

    @Override
    public void didEnterRegion(Region arg0) {
        // In this example, this class sends a notification to the user whenever a Beacon
        // matching a Region (defined above) are first seen.
        Log.d(TAG, "did enter region.");

        if (isActivityVisible()) {
            // If the Monitoring Activity is visible, we log info about the beacons we have
            // seen on its display
            Log.d(TAG, "Activity is visible.");
            Intent intent = new Intent(this, PhotoActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(intent);
        } else {
            // If we have already seen beacons before, but the monitoring activity is not in
            // the foreground, we send a notification to the user on subsequent detections.
            Log.d(TAG, "Sending notification.");
            sendNotification(items.get(0).getText());
        }
    }

    @Override
    public void didExitRegion(Region arg0) {
        // Don't care
    }

    private void sendNotification(String text) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setContentTitle("HiBeacon")
                        .setContentText(text)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setDefaults(Notification.DEFAULT_SOUND);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(new Intent(this, PhotoActivity.class));
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        builder.setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }
}