package com.example.netballapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.netballapp.Model.SessionManager;
import com.example.netballapp.R;

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

    public void onSetUpNewGameClicked(View view) {
        Intent intent = new Intent(DashboardActivity.this, SetUpNewGameActivity.class);
        startActivity(intent);
        finish();
    }

    public void onLogoutClicked(View view) {
        SessionManager.logout(this);
    }

    public void onManageGamesClicked(View view) {
        Intent intent = new Intent(DashboardActivity.this, ManageGamesActivity.class);
        startActivity(intent);
        finish();
    }
}