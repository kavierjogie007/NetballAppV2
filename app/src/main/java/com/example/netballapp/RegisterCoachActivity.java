package com.example.netballapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterCoachActivity extends AppCompatActivity {
    private EditText edtFirstName, edtSurname, edtUsername, edtPassword, edtConfirmPassword;
    private Spinner spinnerRole;
    private SuperbaseAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_coach);

        edtFirstName = findViewById(R.id.edtFirstName);
        edtSurname = findViewById(R.id.edtSurname);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        spinnerRole = findViewById(R.id.spinnerRole);

        String[] roles = {"Head Coach", "Assistant Coach"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);

        api = RetrofitClient.getClient().create(SuperbaseAPI.class);
    }

    public void onBackClicked(View view) {
            Intent intent = new Intent(RegisterCoachActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
    }


    public void onRegisterClicked(View view) {
        String firstName = edtFirstName.getText().toString().trim();
        String surname = edtSurname.getText().toString().trim();
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();
        String role = spinnerRole.getSelectedItem().toString();

        if (firstName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return;
        }

        Coach coach = new Coach();
        coach.coach_firstname = firstName;
        coach.coach_surname = surname;
        coach.coach_username = username;
        coach.coach_password = password;
        coach.coach_role = role;

        Call<List<Coach>> call = api.registerCoach(coach);
        call.enqueue(new Callback<List<Coach>>() {
            @Override
            public void onResponse(Call<List<Coach>> call, Response<List<Coach>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Coach savedCoach = response.body().get(0); // ✅ Use the returned coach
                    Toast.makeText(RegisterCoachActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();

                    getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                            .edit()
                            .putLong("coach_ID", savedCoach.coach_ID)
                            .apply();

                    Intent intent = new Intent(RegisterCoachActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(RegisterCoachActivity.this, "Registration failed. Username might already exist.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Coach>> call, Throwable t) {
                Toast.makeText(RegisterCoachActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}