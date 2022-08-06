package com.example.smartord.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartord.Classes.DbOrder;
import com.example.smartord.Classes.Order;
import com.example.smartord.Classes.User;
import com.example.smartord.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.HashMap;

public class NewReservationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private DatePickerDialog.OnDateSetListener setListener;
    private Button chooseDateBtn;
    private Spinner spinner;
    private Button submitBtn;
    private DatabaseReference reference;
    private FirebaseUser user;
    private DatabaseReference userReferance;
    private String fullDateWithTimeStamp = "";
    private String dateFetched;
    private String timeFetched;
    private EditText amountOfAttendeds;
    private TextView chooseDateTV;
    private ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_reservation);

        // set action bar text and back btn option
        actionBar = getSupportActionBar();
        actionBar.setTitle("New Reservation");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        // find views from layout xml
        chooseDateTV = findViewById(R.id.dateChosenET);
        chooseDateBtn = findViewById(R.id.chooseDateBtn);
        submitBtn = findViewById(R.id.submitReservationBtn);
        amountOfAttendeds = findViewById(R.id.numberOfPeopleET);
        spinner = findViewById(R.id.spinner);

        // get logged user from db
        user = FirebaseAuth.getInstance().getCurrentUser();
        userReferance = FirebaseDatabase.getInstance().getReference().child("users");
        reference = FirebaseDatabase.getInstance().getReference().child("Orders");


        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        // create spinner with pre-defined values (defined in values -> strings.xml
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.hours, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);


        // choose date btn clicked
        chooseDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // show date picker
                DatePickerDialog datePickerDialog = new DatePickerDialog(NewReservationActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, setListener, year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();

            }
        });


        setListener = new DatePickerDialog.OnDateSetListener() { // date picked listener
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = dayOfMonth + "-" + month + "-" + year;
                Log.i("Date", date);
                dateFetched = date;
                chooseDateTV.setText(dateFetched);

            }
        };


        /**
         * After submitting the data need to perform:
         * 1. Check if there is instance in firebase that matches the date with the time
         * 2. if so, check how many people already there in the order
         * 3. if there is a place, or there is no instance on firebase:
         *  - Create new instance (if there is no instance)
         *  - show the correct fragment (if yes or no)
         *
         */
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create the string from date and time
                StringBuilder sb = new StringBuilder();
                LocalDateTime timeNow = LocalDateTime.now();
                int currentYear = timeNow.getYear();
                int currentMonth = timeNow.getMonth().getValue();
                int currentDay = timeNow.getDayOfMonth();
                int currentHour = timeNow.getHour()+3;

                String[] splitted = dateFetched.split("-"); // date format : dd-mm-yyyy
                if (null == dateFetched || timeFetched.equals("Select")) { // check validty
                    Toast toast = Toast.makeText(getApplicationContext(), "One of the fields is empty!!", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (currentYear <= Integer.valueOf(splitted[2]) && currentMonth <= Integer.valueOf(splitted[1]) && currentDay <= Integer.valueOf(splitted[0])) {
                    sb.append(dateFetched).append("T").append(timeFetched);
                    fullDateWithTimeStamp = sb.toString();

                    if(currentYear == Integer.valueOf(splitted[2]) && currentMonth == Integer.valueOf(splitted[1]) && currentDay == Integer.valueOf(splitted[0])){
                        if(currentHour>Integer.parseInt(timeFetched)){
                            Toast toast = Toast.makeText(getApplicationContext(), "Cannot reserve for past time today", Toast.LENGTH_SHORT);
                            toast.show();
                            return;
                        }
                    }

                    // check in db
                    reference.child(fullDateWithTimeStamp).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) { // there is  a reservation for the specific time and date
                                // getting the instance from the db
                                Object value = snapshot.getValue();

                                HashMap<String, String> hash = (HashMap<String, String>) value;
                                String amountOfPeople = "0";
                                amountOfPeople = hash.get("amountOfPeople");
                                if (Integer.parseInt(amountOfPeople + amountOfAttendeds.getText().toString()) > 50) { // restaurant full
                                    startActivity(new Intent(getApplicationContext(), SuccessFailureActivity.class).putExtra("IsComplete", "false"));
                                } else { // reservation can be saved
                                    User userLogged = User.getInstance();
                                    HashMap<String, String> reservations = userLogged.getReservations();
                                    reservations.put(fullDateWithTimeStamp, amountOfAttendeds.getText().toString());
                                    FirebaseDatabase.getInstance().getReference("Users")
                                            .child(user.getUid())
                                            .setValue(userLogged).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });
                                    startActivity(new Intent(getApplicationContext(), SuccessFailureActivity.class).putExtra("IsComplete", "true"));
                                }


                            } else { // first reservation for that date
                                DbOrder order = new DbOrder(String.valueOf(amountOfAttendeds.getText().toString()), String.valueOf(1));
                                FirebaseDatabase.getInstance().getReference("Orders")
                                        .child(fullDateWithTimeStamp)
                                        .setValue(order).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) { // if created the reservation use the successful activity -> fragment
                                            if (Integer.parseInt(amountOfAttendeds.getText().toString()) > 50) {

                                                startActivity(new Intent(getApplicationContext(), SuccessFailureActivity.class).putExtra("IsComplete", "false"));
                                            } else {
                                                User userLogged = User.getInstance();
                                                HashMap<String, String> reservations = userLogged.getReservations();
                                                reservations.put(fullDateWithTimeStamp, amountOfAttendeds.getText().toString());

                                              // -------------------
                                                Order o = new Order();
                                                o.setAttendsNumber(amountOfAttendeds.getText().toString());
                                                o.setOrderDate(fullDateWithTimeStamp);

                                                FirebaseDatabase.getInstance().getReference("Users")
                                                        .child(user.getUid()).child("reservations")
                                                        .setValue(reservations).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                    }
                                                });

                                                startActivity(new Intent(getApplicationContext(), SuccessFailureActivity.class).putExtra("IsComplete", "true"));
                                            }


                                        }
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else { // the reservation date has passed
                    Toast toast = Toast.makeText(getApplicationContext(), "Cannot reserve for past date", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }


    // spinner save selected value
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.i("Spinner", parent.getItemAtPosition(position).toString());
        String timeSplited = parent.getItemAtPosition(position).toString().split(":")[0];
        if (timeSplited != null) {
            timeFetched = timeSplited;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

}
