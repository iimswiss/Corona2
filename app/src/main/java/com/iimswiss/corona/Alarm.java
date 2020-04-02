package com.iimswiss.corona;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import static android.content.Context.NOTIFICATION_SERVICE;

public class Alarm extends BroadcastReceiver {
    Comm comm = new Comm();
    int i = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        //sendLocation
        //Toast.makeText(context, "Alarm Called. Next alarm will be after 5 minutes.", Toast.LENGTH_LONG).show();
        comm.SendLocations();
        SetAlarm(context);
    }

    public void ClearAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, Alarm.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        if(alarmManager != null){
            alarmManager.cancel(pi);
        }
    }

    public void SetAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);

        Date dat = new Date();
        Calendar cal_alarm = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dat);
        cal_alarm.setTime(dat);
        cal_alarm.set(Calendar.HOUR_OF_DAY, 10);
        cal_alarm.set(Calendar.MINUTE, 43);
        cal_alarm.set(Calendar.SECOND, 0);

        if (cal_alarm.before(calendar)) {
            cal_alarm.add(Calendar.DATE, 1);
        }
        Intent i = new Intent(context, Alarm.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        if (alarmManager != null) {
            //reset alarm for next 5 minutes.
           alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, 300000, pi);
        }
    }
}
