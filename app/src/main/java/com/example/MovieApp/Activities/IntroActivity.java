package com.example.MovieApp.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.MovieApp.R;

public class IntroActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "MyPrefs";
    private static final String PREF_PHONE_NUMBER = "PREF_PHONE_NUMBER";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkLoggedIn();
        setContentView(R.layout.activity_intro);
        Button getinBtn = findViewById(R.id.getinBtn);
        getinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(IntroActivity.this, LoginActivity.class));
            }
        });
    }

    private void checkLoggedIn(){
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String phoneNumber = sharedPreferences.getString(PREF_PHONE_NUMBER, "");
        if(!phoneNumber.isEmpty()){
            Intent intent = new Intent(IntroActivity.this, MainActivity.class);
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            startActivity(intent);
        }
    }
}