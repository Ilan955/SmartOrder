package com.example.smartord.Modal;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartord.Classes.Order;
import com.example.smartord.Classes.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OrderViewModal extends ViewModel {


    private DatabaseReference reference;
    private FirebaseUser user;
    private MutableLiveData<List<Order>> mutableLiveData;
    private User userRef;
    private String userId;

    public LiveData<List<Order>> getOrderList() {

        if (mutableLiveData == null) {
            user = FirebaseAuth.getInstance().getCurrentUser();
            userId = user.getUid();
            mutableLiveData = new MutableLiveData<>();
            userRef = User.getInstance();
            List<Order> ordersList = new ArrayList<>();
            reference = FirebaseDatabase.getInstance().getReference().child("Users");
            reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Object value = snapshot.getValue();
                    User userLogged = User.getInstance();
                    if (value != null) {
                        HashMap<String, String> hash = (HashMap<String, String>) value;
                        userLogged.setFullName(hash.get("fullName"));
                        userLogged.setPhoneNumber(hash.get("phoneNumber"));
                        String keyValueFromRes = "";
                        String valueFromOld = "";
                        userLogged.setEmailAddress(hash.get("emailAddress"));
                        Object reser = hash.get("reservations");
                        if (null != reser) {
                            HashMap<String, String> reservationHash = (HashMap<String, String>) reser;
                            if (!reservationHash.isEmpty()) {
                                userLogged.setReservations(reservationHash);
                                for (String key : reservationHash.keySet()) {
                                    String amountOfAttendeds = reservationHash.get(key);
                                    Order order = new Order();
                                    order.setAttendsNumber(amountOfAttendeds);
                                    order.setOrderDate(key);
                                    ordersList.add(order);
                                }
                            }
                        }
                        mutableLiveData.setValue(ordersList);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        return mutableLiveData;
    }

    public boolean deleteOrder(int position) {
        if (null != mutableLiveData.getValue()) {
            List<Order> orders = new ArrayList<>(mutableLiveData.getValue());
            Order orderToDelete = orders.get(position);
            String orderDate = orderToDelete.getOrderDate().split("T")[0];
            String[] splitted = orderDate.split("-");
            LocalDateTime timeNow = LocalDateTime.now();
            int currentYear = timeNow.getYear();
            int currentMonth = timeNow.getMonth().getValue();
            int currentDay = timeNow.getDayOfMonth();
            if ((currentYear > Integer.valueOf(splitted[2]))) { // if year is smaller  continue
                return false;
            } else if ((currentMonth > Integer.valueOf(splitted[1]))) { // if month is smaller continue
                return false;


            } else if ((currentMonth == Integer.valueOf(splitted[1]))) { // the same month
                if ((currentDay > Integer.valueOf(splitted[0]))) {
                    return false;
                }
            }


            orders.remove(position);
            mutableLiveData.setValue(orders);
            HashMap<String, String> orderHash = new HashMap<>();
            for (Order order : orders) {
                orderHash.put(order.getOrderDate(), order.getAttendsNumber());
            }
            //set the new list of reservation without the deleted one
            userRef.setReservations(orderHash);
            reference.child(userId).child("reservations").setValue(orderHash);
            return true;

        }
        return false;

    }
}
