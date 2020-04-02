package com.iimswiss.corona;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Calendar;
import java.util.Date;

public class FirstWorker extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_worker);
        int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 1;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
            if (ContextCompat.checkSelfPermission(FirstWorker.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(FirstWorker.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);
            }
            else {
                showMessage("Sorry!","We need location permission to continue.");
                finish();
            }
        }

        //start location tracker here
        Alarm alarm = new Alarm();
        alarm.SetAlarm(FirstWorker.this);
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
                        comm.SaveSettings("phone","");
                        comm.SaveSettings("email","");
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
        //https://stackoverflow.com/questions/12957645/setting-a-repeating-alarm-in-android
        //SetAlarm(FirstWorker.this);

//        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
//            int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 1;
//            if (ContextCompat.checkSelfPermission(FirstWorker.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(FirstWorker.this,
//                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                        MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);
//            } else {
//
//                //startupProcess();
//            }
//        } else {
//            //startupProcess();
//        }
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

}
