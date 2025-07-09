package com.example.netballapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
    }

    public void onCoachProfileClicked(View view) {
        Intent intent = new Intent(DashboardActivity.this, ManageCoachProfile.class);
        startActivity(intent);
        finish();
    }

    public void onPlayerProfilesClicked(View view) {
        Intent intent = new Intent(DashboardActivity.this, Player_Profiles.class);
        startActivity(intent);
        finish();
    }
}