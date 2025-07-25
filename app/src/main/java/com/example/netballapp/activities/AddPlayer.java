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

public class AddPlayer extends AppCompatActivity
{
    private EditText edtFirstName, edtSurname, edtPlayerNumber, edtDOB, edtHeight;
    private Spinner spinnerPosition;
    private SuperbaseAPI api;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_player);

        //testing
        edtFirstName = findViewById(R.id.edtFirstName);
        edtSurname = findViewById(R.id.edtSurname);
        edtPlayerNumber = findViewById(R.id.edtPlayerNumber);
        edtDOB = findViewById(R.id.edtDOB);
        edtHeight = findViewById(R.id.edtHeight);
        spinnerPosition=findViewById(R.id.spinnerPosition);

        String[] positions = {"GS", "GA","WA","C","WD","GD","GK"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, positions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPosition.setAdapter(adapter);

        api = RetrofitClient.getClient().create(SuperbaseAPI.class);
    }

    public void onBackClicked(View view)
    {
        Intent intent = new Intent(AddPlayer.this, Player_Profiles.class);
        startActivity(intent);
        finish();
    }

    public void onAddPlayerToDBClicked(View view) {
        String firstName = edtFirstName.getText().toString().trim();
        String surname = edtSurname.getText().toString().trim();
        String strPlayerNumber=edtPlayerNumber.getText().toString().trim();
        String dateOfBirth=edtDOB.getText().toString().trim();
        String strHeight = edtHeight.getText().toString().trim();
        String position = spinnerPosition.getSelectedItem().toString();

        Integer playerNumber = null;
        if (!strPlayerNumber.isEmpty()) {
            try {
                playerNumber = Integer.parseInt(strPlayerNumber);
            } catch (NumberFormatException e) {
                // Handle the case where input is not a valid number
                Toast.makeText(this, "Please enter a valid player Number", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Player Number field is empty", Toast.LENGTH_SHORT).show();
        }

        Integer height = null;
        if (!strHeight.isEmpty()) {
            try {
                height = Integer.parseInt(strHeight);
            } catch (NumberFormatException e) {
                // Handle the case where input is not a valid number
                Toast.makeText(this, "Please enter a valid height", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Height field is empty", Toast.LENGTH_SHORT).show();
        }

        if (firstName.isEmpty() || surname.isEmpty() || dateOfBirth.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Player player = new Player();
        player.player_FirstName=firstName;
        player.player_Surname=surname;
        player.player_Number=playerNumber;
        player.player_DOB=dateOfBirth;
        player.player_Height=height;
        player.player_position = position;

        Call<List<Player>> call = api.registerPlayer(player);
        call.enqueue(new Callback<List<Player>>() {
            @Override
            public void onResponse(Call<List<Player>> call, Response<List<Player>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Toast.makeText(AddPlayer.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddPlayer.this, Player_Profiles.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(AddPlayer.this, "Registration failed. Username might already exist.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(AddPlayer.this, response.toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Player>> call, Throwable t) {
                Toast.makeText(AddPlayer.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}