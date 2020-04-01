package com.iimswiss.corona;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;

public class Location extends Service {
    Comm comm = new Comm();
    static Context context;
    public Location() {
    }

    @Override
    public IBinder onBind(Intent intent) {
         return new Binder();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//        locationRequestSync();
//        startMonitoring();
//        appVersionMonitor();
//        startLocationAlarm();
//        startCheckUpdateAlarm();
//        startCheckSwissTextUpdateAlarm();
//        timerCheckSwissText();
//        checkDbForPendingUdpData();
//        checkDbErrLogPending();
//        initCrashlytics();
//        showStickyNotification();
        return START_STICKY;
    }
    void startLocationAlarm() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 15);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Intent intent = new Intent(this, Location.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            //repeat after every 5 minutes
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000*60*5 , pi); // Millisec * Second * Minute
        }

    }
    /*private void showStickyNotification() {
        try {
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.ic_dc_notification)
                            .setContentTitle("DC")
                            .setContentText("DC is running");
            Intent targetIntent = new Intent(context, MainActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(contentIntent);
            builder..flags = Notification.FLAG_ONGOING_EVENT;
            NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (nManager != null) {
                nManager.notify(1710121223, builder.build());
            }
        } catch (Exception e) {
            try {
                Crashlytics.logException(e);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }*/


}
