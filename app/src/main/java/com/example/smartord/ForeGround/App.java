package com.example.smartord.ForeGround;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.smartord.Activities.HomePageActivity;

// class will run on start -> initialize the notification channel for later
public class App extends Application {

    public static final String CHANNEL_ID = "chanel";
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();

    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel service = new NotificationChannel(
                    CHANNEL_ID,"ch", NotificationManager.IMPORTANCE_DEFAULT
                    );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(service);
            Log.i("Hello","Finished create channel");

        }
    }
}
