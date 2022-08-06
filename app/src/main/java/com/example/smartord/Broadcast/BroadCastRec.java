package com.example.smartord.Broadcast;

import static com.example.smartord.ForeGround.App.CHANNEL_ID;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.LiveData;

import com.example.smartord.Activities.HomePageActivity;
import com.example.smartord.Classes.Order;
import com.example.smartord.Modal.OrderViewModal;
import com.example.smartord.R;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class BroadCastRec extends BroadcastReceiver {
    LiveData<List<Order>> orderList;

    public BroadCastRec(LiveData<List<Order>> orderList) {
        this.orderList = orderList;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        // listen to "Broad" broadcast event
        if (intent.getAction().equals("Broad")) {
            // get values from intent extra
            Intent notificationIntent = new Intent(context, HomePageActivity.class);
            String numberOfAttendees = intent.getStringExtra("number");
            String dateOfReservation = intent.getStringExtra("date");


            try { // create the notification and notify to user
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
                Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID).setContentTitle("You have an upcoming  reservation soon!").setContentText("Date:" + dateOfReservation + "    Number of attendees:" + numberOfAttendees).setContentIntent(pendingIntent).setAutoCancel(true).setSmallIcon(R.drawable.delete_item).build();
                NotificationManagerCompat.from(context).notify(1, notification);
            } catch (Exception e) {
                Log.e("BroadCast", "Error on notification: " + e.getMessage());
            }
        }
    }

}
