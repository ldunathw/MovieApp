package com.example.MovieApp.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.MovieApp.Database.UserDAO;
import com.example.MovieApp.Model.User;
import com.example.MovieApp.R;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameEdt, passwordEdt;
    private UserDAO userDAO;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "MyPrefs";
    private static final String PREF_PHONE_NUMBER = "PREF_PHONE_NUMBER";
    private static final String PREF_USER_NAME = "PREF_USER_NAME";
    private static final String PREF_USER_EMAIL = "PREF_USER_EMAIL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEdt = findViewById(R.id.editTextTextPhoneNumber); // Corrected ID reference
        passwordEdt = findViewById(R.id.editTextPassword);

        userDAO = new UserDAO(this); // Initialize UserDAO
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        findViewById(R.id.loginBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        // Click listener for "Sign Up" TextView
        TextView textViewRegister = findViewById(R.id.textViewRegister);
        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        // Click listener for "Forgot Password?" TextView
        findViewById(R.id.textViewForgotPassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    private void login() {
        String phoneNumber = usernameEdt.getText().toString().trim();
        String password = passwordEdt.getText().toString().trim();

        if (TextUtils.isEmpty(phoneNumber)) {
            usernameEdt.setError("Please enter your phone number");
            usernameEdt.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEdt.setError("Please enter your password");
            passwordEdt.requestFocus();
            return;
        }

        userDAO.open(); // Open the database connection
        User user = userDAO.login(phoneNumber, password);
        userDAO.close(); // Close the database connection

        if (user != null) {
            // Login successful, save phone number in SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(PREF_PHONE_NUMBER, phoneNumber);
            editor.apply();

            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            startActivity(intent);
            finish();
        } else {
            // Login failed
            Toast.makeText(this, "Invalid phone number or password", Toast.LENGTH_SHORT).show();
        }
    }

    // This method is required for the onClick attribute in XML for "Sign Up" TextView
    public void onRegisterClick(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}
