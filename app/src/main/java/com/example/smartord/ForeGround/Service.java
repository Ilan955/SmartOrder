package com.example.smartord.ForeGround;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.smartord.Classes.Order;
import com.example.smartord.Classes.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Service extends android.app.Service {

    private User user;
    private List<Order> listOfOrders;
    private List<Order> alreadyNotified;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    // when calling on start service
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        CheckerThread thread = new CheckerThread();
        alreadyNotified = new ArrayList<>();
        user = User.getInstance();
        thread.start();


        return START_NOT_STICKY;

    }

// check every time for new reservation to notify
    public class CheckerThread extends Thread {
        public void run() {

            while (true) {
                while (user.getReservations().isEmpty()) {
                    Log.i("Hello", "Waiting  for reservations");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Log.e("Thread","Error accrued on thread run: {}"+e.getMessage());
                    }
                }
                Log.i("Hello", "Running");
                listOfOrders = new ArrayList<>();
                listOfOrders = getUpdatedUserReservations(user.getReservations());
                Order order = checkReservationInRange(listOfOrders);
                if (null != order) {
                    Intent intent = new Intent();
                    intent.setAction("Broad");
                    intent.putExtra("number", order.getAttendsNumber());
                    intent.putExtra("date", order.getOrderDate().split("T")[0]);
                    sendBroadcast(intent);
                }
                try {
                    Thread.sleep(60000); // sleep for one minute
                } catch (InterruptedException e) {
                    Log.i("Interruption","Thread has been interrupted");
                }
            }
        }
    }

    // get current user reservations
    private List<Order> getUpdatedUserReservations(HashMap<String, String> reservationHash) {
        List<Order> orderList = new ArrayList<>();
        if (listOfOrders.isEmpty()) {

            for (String key : reservationHash.keySet()) {
                String amountOfAttendeds = reservationHash.get(key);
                Order order = new Order();
                order.setAttendsNumber(amountOfAttendeds);
                order.setOrderDate(key);
                orderList.add(order);
            }
        }
        return orderList;
    }

    // check reservation is in the 3 hours range
    private Order checkReservationInRange(List<Order> orderList) {
        LocalDateTime timeNow = LocalDateTime.now();
        if (orderList != null) {
            for (Order order : orderList) {
                if (!checkIfExists(order)) {
                    String orderDate = order.getOrderDate();
                    String[] timeSplit = orderDate.split("T");
                    String orderDateSplit = timeSplit[0];
                    String hoursTimeSplit = timeSplit[1];
                    int currentDay = timeNow.getDayOfMonth();
                    int currentMonth = timeNow.getMonth().getValue();
                    int currentYear = timeNow.getYear();
                    int currentHour = timeNow.getHour()+3;
                    // orderDate split : dd-MM-yyyy
                    String[] splitDate = orderDateSplit.split("-");
                    if (currentDay == Integer.parseInt(splitDate[0]) && currentMonth == Integer.parseInt(splitDate[1]) && currentYear == Integer.parseInt(splitDate[2])) {
                        if (Integer.parseInt(hoursTimeSplit) - 3 <= currentHour) {
                            alreadyNotified.add(order);
                            return order;
                        }
                    }
                }
            }
        }

        return null;
    }

    // prevent notify the same order more  than once
    private boolean checkIfExists(Order order) {
        for (Order orderNotified : alreadyNotified) {
            if (orderNotified.getOrderDate().equals(order.getOrderDate()) && orderNotified.getAttendsNumber().equals(order.getAttendsNumber())) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
