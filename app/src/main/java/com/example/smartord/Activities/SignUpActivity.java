package com.example.smartord.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.smartord.R;
import com.example.smartord.Classes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    EditText emailTextBox;
    EditText passwordTextBox;
    EditText nameTextBox;
    EditText phoneNumberTextBox;
    ActionBar actionBar;
    Button signUpBtn;
    FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        // defining teh edit text that are the new member section


        emailTextBox = findViewById(R.id.EmailEt);
        passwordTextBox = findViewById(R.id.PasswordET);
        nameTextBox = findViewById(R.id.FullNameEt);
        phoneNumberTextBox = findViewById(R.id.PhoneNumberET);
        signUpBtn = findViewById(R.id.newMemberbtn);
        mAuth = FirebaseAuth.getInstance();

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        actionBar = getSupportActionBar();
        actionBar.setTitle("New Member");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });


    }

    public void registerUser() {

        String email = emailTextBox.getText().toString().trim();
        String userName = nameTextBox.getText().toString().trim();
        String password = passwordTextBox.getText().toString().trim();
        String phoneNumber = phoneNumberTextBox.getText().toString().trim();

        // --------- Check input validity ------------------ //
        if (userName.isEmpty()) {
            Toast toast = Toast.makeText(getApplicationContext(), "Full Name Is Empty!", Toast.LENGTH_SHORT);
            toast.show();
            return;

        }
        if (phoneNumber.isEmpty()) {
            Toast toast = Toast.makeText(getApplicationContext(), "Phone number is empty", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        if (email.isEmpty()) {
            Toast toast = Toast.makeText(getApplicationContext(), "Email is empty", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast toast = Toast.makeText(getApplicationContext(), "Please Enter valid email address", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        if (password.isEmpty()) {
            Toast toast = Toast.makeText(getApplicationContext(), "Password is Empty", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        if (password.length() < 6) {

            Toast toast = Toast.makeText(getApplicationContext(), "Password is too short", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

// -------------- END check input validity ---------------- //


        // attempt to create a user
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    User user = User.getInstance();
                    user.setEmailAddress(email);
                    user.setFullName(userName);
                    user.setPhoneNumber(phoneNumber);
                    HashMap<String,String> hash = new HashMap<>();
                    user.setReservations(hash);
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast toast = Toast.makeText(getApplicationContext(), "Successfully created user", Toast.LENGTH_SHORT);
                                toast.show();

                            }
                        }
                    });
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(), "Ther was an eeror while trying to create the user", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });


    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}