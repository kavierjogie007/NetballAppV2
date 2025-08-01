package com.example.netballapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.netballapp.Model.Coach;
import com.example.netballapp.R;
import com.example.netballapp.api.RetrofitClient;
import com.example.netballapp.api.SuperbaseAPI;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText edtUsername, edtPassword;
    private SuperbaseAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtUsername = findViewById(R.id.username);
        edtPassword = findViewById(R.id.password);

        // Gets Retrofit API instance
        api = RetrofitClient.getClient().create(SuperbaseAPI.class);
    }

    public void onLoginClicked(View view) {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        // Calls loginCoach endpoint
        Call<List<Coach>> call = api.loginCoach("eq." + username, "eq." + password);

        call.enqueue(new Callback<List<Coach>>() {
            @Override
            public void onResponse(Call<List<Coach>> call, Response<List<Coach>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Coach coach = response.body().get(0);

                    // Saves coach ID
                    getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                            .edit()
                            .putLong("coach_ID", coach.getCoach_ID())
                            .apply();

                    Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Coach>> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Login failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onRegisterCoachClicked(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterCoachActivity.class);
        startActivity(intent);
        finish();
    }
}