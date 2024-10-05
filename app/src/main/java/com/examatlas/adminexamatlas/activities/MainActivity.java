package com.examatlas.adminexamatlas.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.examatlas.adminexamatlas.R;
import com.examatlas.adminexamatlas.extraClasses.SessionManager;

public class MainActivity extends AppCompatActivity {
    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(MainActivity.this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (sessionManager.IsLoggedIn()) {
                    Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    finishAffinity();
                }else {
                    Intent intent = new Intent(MainActivity.this,SecondActivity.class);
                    startActivity(intent);
                    finishAffinity();
                }
            }
        },2000);
    }
}