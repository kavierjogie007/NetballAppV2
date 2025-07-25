package com.example.netballapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.netballapp.Model.Coach;
import com.example.netballapp.R;
import com.example.netballapp.api.RetrofitClient;
import com.example.netballapp.api.SuperbaseAPI;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ManageCoachProfile extends AppCompatActivity {
    private EditText edtFirstName, edtSurname, edtUsername, edtPassword, edtConfirmPassword;
    private Spinner spinnerRole;
    private long currentCoachId;
    private SuperbaseAPI api;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_coach_profile);

        // Retrieve coach ID from SharedPreferences
        currentCoachId = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                .getLong("coach_ID", -1);

        if (currentCoachId == -1) {
            Toast.makeText(this, "Coach ID not found. Please log in again.", Toast.LENGTH_SHORT).show();
            finish(); // Optionally redirect to login screen
            return;
        }

        //Set up UI
        edtFirstName = findViewById(R.id.edtFirstName);
        edtSurname = findViewById(R.id.edtSurname);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        spinnerRole = findViewById(R.id.spinnerRole);

        String[] roles = {"Head Coach", "Assistant Coach"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);

        // Get the Retrofit API instance
        api = RetrofitClient.getClient().create(SuperbaseAPI.class);

        // Load coach profile using Retrofit
        loadCoachProfile(adapter);
    }
    private void loadCoachProfile(ArrayAdapter<String> adapter) {
        Call<List<Coach>> call = api.getCoachById("eq." + currentCoachId);
        call.enqueue(new Callback<List<Coach>>() {
            @Override
            public void onResponse(Call<List<Coach>> call, Response<List<Coach>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Coach coach = response.body().get(0);
                    edtFirstName.setText(coach.coach_firstname);
                    edtSurname.setText(coach.coach_surname);
                    edtUsername.setText(coach.coach_username);
                    edtPassword.setText(coach.coach_password);
                    edtConfirmPassword.setText(coach.coach_password);

                    int spinnerPosition = adapter.getPosition(coach.coach_role);
                    spinnerRole.setSelection(spinnerPosition);
                } else {
                    Toast.makeText(ManageCoachProfile.this, "Coach profile not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Coach>> call, Throwable t) {
                Toast.makeText(ManageCoachProfile.this, "Failed to load profile: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void onUpdateProfileClicked(View view) {
        String firstname = edtFirstName.getText().toString().trim();
        String surname = edtSurname.getText().toString().trim();
        String role = spinnerRole.getSelectedItem().toString();
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return;
        }

        Coach updatedCoach = new Coach();
        updatedCoach.coach_firstname = firstname;
        updatedCoach.coach_surname = surname;
        updatedCoach.coach_role = role;
        updatedCoach.coach_username = username;
        updatedCoach.coach_password = password;

        Call<List<Coach>> call = api.updateCoachProfile("eq." + currentCoachId, updatedCoach);
        call.enqueue(new Callback<List<Coach>>() {
            @Override
            public void onResponse(Call<List<Coach>> call, Response<List<Coach>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Toast.makeText(ManageCoachProfile.this, "Profile updated!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ManageCoachProfile.this, "Update failed.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Coach>> call, Throwable t) {
                Toast.makeText(ManageCoachProfile.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onBackClicked(View view) {
        Intent intent = new Intent(ManageCoachProfile.this, DashboardActivity.class);
        startActivity(intent);
        finish(); // Finish LoginActivity so it's not in the back stack
    }
}