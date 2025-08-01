package com.example.netballapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.netballapp.Model.Player;
import com.example.netballapp.R;
import com.example.netballapp.api.RetrofitClient;
import com.example.netballapp.api.SuperbaseAPI;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdatePlayerProfile extends AppCompatActivity
{
    private EditText edtFirstName, edtSurname, edtPlayerNumber, edtDOB, edtHeight;
    private Spinner spinnerPosition;
    private long currentPlayerId;
    private SuperbaseAPI api;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_player_profile);

        currentPlayerId = getIntent().getLongExtra("player_id",-1);

        Toast.makeText(this, "Player ID: " + currentPlayerId, Toast.LENGTH_SHORT).show();

        if (currentPlayerId == -1) {
            Toast.makeText(this, "Player ID not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        edtFirstName = findViewById(R.id.edtFirstName);
        edtSurname = findViewById(R.id.edtSurname);
        edtPlayerNumber = findViewById(R.id.edtPlayerNumber);
        edtDOB = findViewById(R.id.edtDOB);
        edtHeight = findViewById(R.id.edtHeight);
        spinnerPosition = findViewById(R.id.spinnerPosition);

        String[] positions = {"GS", "GA","WA","C","WD","GD","GK"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, positions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPosition.setAdapter(adapter);

        // Get the Retrofit API instance
        api = RetrofitClient.getClient().create(SuperbaseAPI.class);

        // Load coach profile using Retrofit
        loadPlayerProfile(adapter);
    }

    private void loadPlayerProfile(ArrayAdapter<String> adapter)
    {
        Call<List<Player>> call = api.getPlayerById("eq." + currentPlayerId);
        call.enqueue(new Callback<List<Player>>() {
            @Override
            public void onResponse(Call<List<Player>> call, Response<List<Player>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Player player = response.body().get(0);
                    edtFirstName.setText(player.getPlayer_FirstName());
                    edtSurname.setText(player.getPlayer_Surname());
                    edtPlayerNumber.setText(String.valueOf(player.getPlayer_Number()));
                    edtDOB.setText(player.getPlayer_DOB());
                    edtHeight.setText(String.valueOf(player.getPlayer_Height()));
                    int spinnerIndex = adapter.getPosition(player.getPlayer_position());
                    spinnerPosition.setSelection(spinnerIndex);
                } else {
                    Toast.makeText(UpdatePlayerProfile.this, "Player profile not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Player>> call, Throwable t) {
                Toast.makeText(UpdatePlayerProfile.this, "Failed to load profile: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onUpdatePlayerToDBClicked(View view) {
        String firstname = edtFirstName.getText().toString().trim();
        String surname = edtSurname.getText().toString().trim();
        String strPlayerNumber = edtPlayerNumber.getText().toString().trim();
        String position= spinnerPosition.getSelectedItem().toString();
        String dob=edtDOB.getText().toString().trim();
        String strHeight=edtHeight.getText().toString().trim();

        Integer playerNumber = null;
        Integer height = null;
        try {
            if (!strPlayerNumber.isEmpty()) {
                playerNumber = Integer.parseInt(strPlayerNumber);
            }
            if (!strHeight.isEmpty()) {
                height = Integer.parseInt(strHeight);
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number or height", Toast.LENGTH_SHORT).show();
            return;
        }

        Player updatedPlayer = new Player(firstname,surname,playerNumber,position,dob,height);

        Call<List<Player>> call = api.updatePlayerProfile("eq." + currentPlayerId, updatedPlayer);
        call.enqueue(new Callback<List<Player>>() {
            @Override
            public void onResponse(Call<List<Player>> call, Response<List<Player>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Toast.makeText(UpdatePlayerProfile.this, "Profile updated!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(UpdatePlayerProfile.this, "Update failed.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Player>> call, Throwable t) {
                Toast.makeText(UpdatePlayerProfile.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onBackClicked(View view) {
        Intent intent = new Intent(UpdatePlayerProfile.this, Player_Profiles.class);
        startActivity(intent);
        finish();
    }
}