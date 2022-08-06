package com.example.smartord.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartord.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class LoginActivity extends AppCompatActivity {

    private TextView registerTV;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private Button loginBtn;
    private EditText email;
    private EditText password;
    private SharedPreferences sp;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // action bar text change
        actionBar = getSupportActionBar();
        actionBar.setTitle("Login");

        // find views from layout xml
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        registerTV = findViewById(R.id.RegisterTV);
        mAuth = FirebaseAuth.getInstance();
        loginBtn = findViewById(R.id.btnLogin);
        email = findViewById(R.id.UserNameET);
        password = findViewById(R.id.PasswordET);

        // check already signed in from shared preferences
        if (sp.getBoolean("StayLoggedIn", false)) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (null != currentUser) {
                startActivity(new Intent(getBaseContext(), HomePageActivity.class));

            }
        }



        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // check for correctness s of the input
                String emailStr = email.getText().toString();
                String passwordStr = password.getText().toString();
                if (emailStr.equals("") || passwordStr.equals("")) {
                    Toast toast = Toast.makeText(getApplicationContext(), "One of the fields is empty!!", Toast.LENGTH_SHORT);
                    toast.show();
                } else { // if fields are valid - log in attempt
                    mAuth.signInWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) { // login successfully  made
                                startActivity(new Intent(getBaseContext(), HomePageActivity.class));

                            } else { // if login failed
                                Toast toast = Toast.makeText(getApplicationContext(), "Credentials are wrong or user not exists!", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }
                    });
                }
            }
        });


        // register btn clicked
        registerTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), SignUpActivity.class);
                startActivity(intent);

            }
        });
    }
}
