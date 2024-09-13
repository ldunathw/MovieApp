package com.example.MovieApp.Activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.MovieApp.Database.UserDAO;
import com.example.MovieApp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextFullName;
    private EditText editTextPhoneNumber;
    private Spinner spinnerGender;
    private EditText editTextBirthday;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonRegister;
    private UserDAO userDAO;

    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextFullName = findViewById(R.id.editTextFullName);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        spinnerGender = findViewById(R.id.spinnerGender);
        editTextBirthday = findViewById(R.id.editTextBirthday);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonRegister = findViewById(R.id.buttonRegister);

        // Setup the spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_arrays, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerGender.setAdapter(adapter);
        spinnerGender.setSelection(0, false);

        userDAO = new UserDAO(this);

        editTextBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userDAO.open();  // Ensure the database is opened here
                registerUser();
                userDAO.close(); // Ensure the database is closed here
            }
        });

        calendar = Calendar.getInstance();
    }

    private void showDatePickerDialog() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateBirthdayEditText();
            }
        };

        // Create a new instance of DatePickerDialog and return it
        new DatePickerDialog(this, dateSetListener,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    private void updateBirthdayEditText() {
        // Format the selected date and set it to editTextBirthday
        String myFormat = "yyyy-MM-dd"; // Specify your format
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        editTextBirthday.setText(sdf.format(calendar.getTime()));
        editTextBirthday.setError(null);
    }

    private void registerUser() {
        String fullName = editTextFullName.getText().toString().trim();
        String phoneNumber = editTextPhoneNumber.getText().toString().trim();
        String gender = spinnerGender.getSelectedItem().toString();
        String birthday = editTextBirthday.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(fullName)) {
            editTextFullName.setError("Please enter your full name");
            editTextFullName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phoneNumber)) {
            editTextPhoneNumber.setError("Please enter your phone number");
            editTextPhoneNumber.requestFocus();
            return;
        }

        // Example phone number validation (adjust as needed)
        if (!isValidPhoneNumber(phoneNumber)) {
            editTextPhoneNumber.setError("Please enter a valid phone number");
            editTextPhoneNumber.requestFocus();
            return;
        }else  if (userDAO.isPhoneNumberExists(phoneNumber)) {
            editTextPhoneNumber.setError("Phone number already exists");
            editTextPhoneNumber.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(gender) || gender.equals("Select Gender")) {
            Toast.makeText(RegisterActivity.this, "Please select your gender", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(birthday)) {
            editTextBirthday.setError("Please select your birthday");
            editTextBirthday.requestFocus();
            return;
        }

        // Example email validation (adjust as needed)
        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Please enter your email");
            editTextEmail.requestFocus();
            return;
        } else if (!isValidEmail(email)) {
            editTextEmail.setError("Please enter a valid email");
            editTextEmail.requestFocus();
            return;
        }else if (userDAO.isEmailExists(email)) {
            editTextEmail.setError("Email already exists");
            editTextEmail.requestFocus();
            return;
        }

        // Example password validation (adjust as needed)
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Please enter a password");
            editTextPassword.requestFocus();
            return;
        } else if (password.length() < 6) {
            editTextPassword.setError("Password must be at least 6 characters");
            editTextPassword.requestFocus();
            return;
        }

        // All inputs are valid, proceed with registration
        long id = userDAO.addUser(fullName, phoneNumber, gender, birthday, email, password);
        if (id > 0) {
            Toast.makeText(this, "User registered successfully", Toast.LENGTH_SHORT).show();
            navigateToLoginActivity();

        } else {
            Toast.makeText(this, "User registration failed", Toast.LENGTH_SHORT).show();
        }
    }

    // Example method to validate phone number (adjust as needed)
    private boolean isValidPhoneNumber(String phoneNumber) {
        // Use regex or any other method to validate phone number
        // For example, simple regex validation for a number of specific format
        Pattern pattern = Pattern.compile("\\d{10}");
        return pattern.matcher(phoneNumber).matches();
    }

    // Example method to validate email (using Android's built-in pattern check)
    private boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
    private void navigateToLoginActivity() {
        Intent intent = new Intent(RegisterActivity.this, com.example.MovieApp.Activities.LoginActivity.class);
        startActivity(intent);
        finish(); // Optional: Close current activity to prevent going back to it on back press
    }
}

