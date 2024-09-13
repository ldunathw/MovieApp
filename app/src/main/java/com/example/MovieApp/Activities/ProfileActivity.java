package com.example.MovieApp.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.MovieApp.Database.UserDAO;
import com.example.MovieApp.Model.User;
import com.example.MovieApp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public class ProfileActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private EditText editTextFullName, editTextPhoneNumber, editTextBirthday, editTextEmail;
    private Spinner spinnerGender;
    Uri selectedImageUri;

    private UserDAO userDAO;
    TextView btnLogOut, buttonUpdate;
    ImageView imgAvt;
    CardView cvSelectImg;
    private Toolbar tbPda;
    private Calendar calendar;
    private SharedPreferences sharedPreferences;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize UI components
        editTextFullName = findViewById(R.id.editTextFullName);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        editTextBirthday = findViewById(R.id.editTextBirthday);
        editTextEmail = findViewById(R.id.editTextEmail);
        spinnerGender = findViewById(R.id.spinnerGender);
        cvSelectImg = findViewById(R.id.cvSelectImg);
        imgAvt = findViewById(R.id.imgAvatar);

        // Initialize UserDAO
        userDAO = new UserDAO(this);

        tbPda = findViewById(R.id.tbPda);
        setSupportActionBar(tbPda);
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        // Set the custom back button icon
        Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.back);
        if (upArrow != null) {
            upArrow.setColorFilter(ContextCompat.getColor(this, android.R.color.black), PorterDuff.Mode.SRC_ATOP);
            Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(upArrow);
        }

        // Enable the Up button
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // Set click listener for the back button in the Toolbar
        tbPda.setNavigationOnClickListener(v -> onBackPressed());

        // Setup the spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_arrays, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerGender.setAdapter(adapter);
        spinnerGender.setSelection(0, false);
        getUserByPhoneNumber();

        btnLogOut = findViewById(R.id.btnLogOut);
        buttonUpdate = findViewById(R.id.buttonUpdate);
        btnLogOut.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        editTextBirthday.setOnClickListener(v -> showDatePickerDialog());

        buttonUpdate.setOnClickListener(v -> {
            userDAO.open();  // Ensure the database is opened here
            updateUser();
            userDAO.close(); // Ensure the database is closed here
        });

        calendar = Calendar.getInstance();
    }

    private void getUserByPhoneNumber() {
        String phoneNumber = sharedPreferences.getString("PREF_PHONE_NUMBER", "");
        User user = null;
        userDAO.open();
        user = userDAO.getUserByPhoneNumber(phoneNumber);
        userDAO.close();
        if (user != null) {
            // Populate UI with user data
            editTextFullName.setText(user.getFullName());
            editTextPhoneNumber.setText(user.getPhoneNumber());
            editTextBirthday.setText(user.getBirthday());
            editTextEmail.setText(user.getEmail());
            if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                imgAvt.setImageURI(Uri.parse(user.getAvatar()));
            } else {
                imgAvt.setImageResource(R.drawable.profile); // Hình ảnh mặc định nếu không có avatar
            }

            // Set selected gender in spinner
            String[] genders = getResources().getStringArray(R.array.gender_arrays); // Assume genders array is defined in strings.xml
            for (int i = 0; i < genders.length; i++) {
                if (genders[i].equalsIgnoreCase(user.getGender())) {
                    spinnerGender.setSelection(i);
                    break;
                }
            }
        } else {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDatePickerDialog() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateBirthdayEditText();
        };

        // Create a new instance of DatePickerDialog and return it
        new DatePickerDialog(this, dateSetListener,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateBirthdayEditText() {
        String myFormat = "yyyy-MM-dd"; // Specify your format
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        editTextBirthday.setText(sdf.format(calendar.getTime()));
        editTextBirthday.setError(null);
    }

    private void updateUser() {
        String fullName = editTextFullName.getText().toString().trim();
        String phoneNumber = editTextPhoneNumber.getText().toString().trim();
        String avatarUri = selectedImageUri != null ? selectedImageUri.toString() : null;
        String gender = spinnerGender.getSelectedItem().toString();
        String birthday = editTextBirthday.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();

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

        if (TextUtils.isEmpty(gender) || gender.equals("Select Gender")) {
            Toast.makeText(this, "Please select your gender", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(birthday)) {
            editTextBirthday.setError("Please select your birthday");
            editTextBirthday.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Please enter your email");
            editTextEmail.requestFocus();
            return;
        } else if (!isValidEmail(email)) {
            editTextEmail.setError("Please enter a valid email");
            editTextEmail.requestFocus();
            return;
        }

        // All inputs are valid, proceed with registration
        long id = userDAO.updateUser(fullName, phoneNumber, avatarUri, gender, birthday, email);
        if (id > 0) {
            Toast.makeText(this, "User update successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "User update failed", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // Handle navigation back
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Quyền đã được cấp, mở thư viện ảnh
                openImagePicker();
            } else {
                // Quyền bị từ chối
                Toast.makeText(this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onChangeAvatarClick(View view) {
        // Kiểm tra quyền trước khi mở thư viện ảnh
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // Quyền đã được cấp, mở thư viện ảnh
            openImagePicker();
        } else {
            // Yêu cầu quyền truy cập
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            imgAvt.setImageURI(selectedImageUri);

            // Cấp quyền truy cập lâu dài cho URI
            getContentResolver().takePersistableUriPermission(selectedImageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            // Lưu URI của avatar vào SharedPreferences
            String phoneNumber = sharedPreferences.getString("PREF_PHONE_NUMBER", "");

            userDAO.open();
            userDAO.updateUserAvatar(phoneNumber, selectedImageUri.toString());
            userDAO.close();
        }
    }
}
