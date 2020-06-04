package com.google.android.gms.example.bannerexample;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;


public class MyService extends Service {

    public static String SERVICE_NOTIFICATION_CHANNEL_ID = "com.google.android.gms.example.bannerexample";
    private BroadcastReceiver mBroadcastReceiver;
    private final static String TAG = MyService.class.getSimpleName();


    @Override
    public void onCreate() {
        super.onCreate();
        initBroadcastReceiver();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel();
                startForegroundForService();
            } else {
                startForeground(1, new Notification());
            }
        }
        return START_STICKY;
    }


    private void initBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        this.mBroadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                long timeStamp = System.currentTimeMillis();
                Log.d(TAG, "ACTION_BATTERY_CHANGED received in foreground service " + timeStamp);
            }
        };
        this.registerReceiver(this.mBroadcastReceiver, intentFilter);
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channelBackground = new NotificationChannel(SERVICE_NOTIFICATION_CHANNEL_ID, "Test channel", NotificationManager.IMPORTANCE_HIGH);
            channelBackground.setDescription("Channel for tests");
            channelBackground.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            // create
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channelBackground);
        }
    }


    private void startForegroundForService() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Intent intent = new Intent(this, NotificationBroadcastReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    1,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );

            String contentTitle = "App running in background";
            String contentText = "Service active";

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, SERVICE_NOTIFICATION_CHANNEL_ID);
            Notification notification = notificationBuilder.setOngoing(false)
                    .setContentTitle(contentTitle)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(contentText))
                    .setPriority(NotificationManager.IMPORTANCE_LOW)
                    .setOnlyAlertOnce(true)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                    .build();

            startForeground(2, notification);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            this.unregisterReceiver(this.mBroadcastReceiver);
        } catch (Exception e) {
            Log.d(TAG, "onDestroy: unregister receiver - " + e.getMessage());
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

}