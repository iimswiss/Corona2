package com.iimswiss.corona;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class Tracker extends Service {
    private static final String CHANNEL_ID = "chanel_id_3434";
    Alarm alarm = new Alarm();
    public Tracker() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showStickyNotification();
        alarm.SetAlarm(AppContext.getAppContext());
        return START_STICKY;
    }
    private void showStickyNotification() {
        createNotificationChannel();
        Intent snoozeIntent = new Intent(this, FirstWorker.class);
        PendingIntent pi =
                PendingIntent.getBroadcast(this, 0, snoozeIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Monitoring")
                .setOngoing(true)
                .setContentText("We will alert...")
                .setOnlyAlertOnce(true)
                .setDeleteIntent(pi)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("You will be alerted for the nearest COVID-19 infection."))
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(354545, builder.build());
    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.text_location_permission);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
