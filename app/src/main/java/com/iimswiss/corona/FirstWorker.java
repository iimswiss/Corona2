package com.iimswiss.corona;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FirstWorker extends AppCompatActivity {
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    Logger logger = new Logger();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_worker);
        int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 1;
        checkPermissionsAndStartTracker();
        //https://stackoverflow.com/questions/12957645/setting-a-repeating-alarm-in-android
        //SetAlarm(FirstWorker.this);
        Button butOptOut = findViewById(R.id.butOptOut);
        butOptOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FirstWorker.this);
                builder.setMessage("Opt out only if you do not want to help saving human beings. Tap on 'I am not helping' to opt out.");
                builder.setPositiveButton("I want to help", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showMessage("Thank You!", "We salute you.");
                    }
                });
                builder.setNegativeButton("I am not helping", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Comm comm = new Comm();
                        comm.SaveSettings("phone", "");
                        comm.SaveSettings("email", "");
                        Alarm alarm1 = new Alarm();
                        alarm1.ClearAlarm(AppContext.getAppContext());
                        Intent intent = new Intent(FirstWorker.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.show();
            }
        });
    }

    private void StartTracker() {
        //start location tracker here
        Alarm alarm = new Alarm();
        alarm.SetAlarm(FirstWorker.this);

    }

    void showMessage(String title, String msg) {
        new android.app.AlertDialog.Builder(FirstWorker.this)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    void checkPermissionsAndStartTracker() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                logger.WriteLog(2004021541,"Os Version: " + android.os.Build.VERSION.SDK_INT + ". It requires custom message to get permission.");
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(FirstWorker.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                logger.WriteLog(2004021542,"Os Version: " + android.os.Build.VERSION.SDK_INT + ", no custom ui required.");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
        else {
            Intent serviceIntent = new Intent(FirstWorker.this  ,Tracker.class);
            startService(serviceIntent);
           // StartTracker();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        Intent serviceIntent = new Intent(FirstWorker.this  ,Tracker.class);
                        startService(serviceIntent);
                    }
                    else {
                        showMessage("Sorry!", "We cannot continue without location permission. (2004021547)");
                    }
                } else {
                    showMessage("Sorry!", "We cannot continue without location permission.");
                    finish();
                }
            }

        }
    }

}
