package com.iimswiss.corona;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

public class Tracker extends Service {
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
        NotificationManager nMN = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification n  = new Notification.Builder(AppContext.getAppContext())
                .setOngoing(true)
                .setContentTitle("Monitoring")
                .setContentText("You will be alerted if come close to COVID-19 infected person.")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .build();
        if(nMN != null)
        {
            try {
                nMN.notify(2565632, n);
            } catch (Exception e) {
                Logger  logger = new Logger();
                logger.WriteLog(2004021703,"Error showing notification: " + e.getMessage());
            }
        }
       /* try {
            Notification.Builder builder =
                    new Notification.Builder(this)
                            .setSmallIcon(R.drawable.ic_launcher_background)
                            .setContentTitle("DC")
                            .setOngoing(true)
                            .setContentText("DC is running");
            Intent targetIntent = new Intent(Tracker.this, MainActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(contentIntent);
          //  builder.mNotification.flags = Notification.FLAG_ONGOING_EVENT;

            NotificationManager nManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

            if (nManager != null) {
                nManager.notify(1710121223, builder.build());
            }
        } catch (Exception e) {
            Logger  logger = new Logger();
            logger.WriteLog(2004021703,"Error showing notification: " + e.getMessage());
        }*/
    }
}
