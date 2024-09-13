package com.example.MovieApp.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.MovieApp.Database.UserDAO;
import com.example.MovieApp.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText usernameEditText, newPasswordEditText;
    private Button resetPasswordButton;

    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        usernameEditText = findViewById(R.id.editTextPhoneNumber);
        newPasswordEditText = findViewById(R.id.editTextNewPassword);
        resetPasswordButton = findViewById(R.id.resetPasswordBtn);

        userDAO = new UserDAO(this); // Initialize UserDAO

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                String newPassword = newPasswordEditText.getText().toString().trim();

                if (username.isEmpty() || newPassword.isEmpty()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Tên đăng nhập và mật khẩu mới là bắt buộc", Toast.LENGTH_LONG).show();
                } else {
                    userDAO.open(); // Open the database connection

                    // Update password
                    int rowsAffected = userDAO.updatePassword(username, newPassword);

                    userDAO.close(); // Close the database connection

                    if (rowsAffected > 0) {
                        Toast.makeText(ForgotPasswordActivity.this, "Mật khẩu đã được đặt lại thành công", Toast.LENGTH_LONG).show();
                        finish(); // Close ForgotPasswordActivity after password reset
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, "Tên đăng nhập không tồn tại", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
}
