package com.example.netballapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.netballapp.Model.Court;
import com.example.netballapp.Model.Player;
import com.example.netballapp.R;
import com.example.netballapp.adapters.PlayerAdapter;
import com.example.netballapp.adapters.PlayerAdapterCourt;
import com.example.netballapp.api.RetrofitClient;
import com.example.netballapp.api.SuperbaseAPI;
import com.example.netballapp.listeners.OnPlayerClickListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SetUpCourtActivity extends AppCompatActivity {
    private List<Player> playerList;
    private PlayerAdapterCourt adapter;
    private RecyclerView lstPlayers;
    private SuperbaseAPI api;
    private Player selectedPlayer;
    private long currentGameId;
    TextView posGA, posGS, posC, posWA, posWD, posGD, posGK;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_court);

        // Retrieve coach ID from SharedPreferences
        currentGameId = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                .getLong("game_ID", -1);

        if (currentGameId == -1) {
            Toast.makeText(this, "Game ID not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        lstPlayers = findViewById(R.id.lstPlayers);
        posGA = findViewById(R.id.posGA);
        posGS = findViewById(R.id.posGS);
        posC = findViewById(R.id.posC);
        posWA = findViewById(R.id.posWA);
        posGD = findViewById(R.id.posGD);
        posGK = findViewById(R.id.posGK);
        posWD=findViewById(R.id.posWD);

        // Setup RecyclerView
        lstPlayers = findViewById(R.id.lstPlayers);
        lstPlayers.setLayoutManager(new LinearLayoutManager(this));

        api = RetrofitClient.getClient().create(SuperbaseAPI.class);

        // Set position click listeners
        posGA.setOnClickListener(v -> assignPlayerToPosition("court_goalAttack"));
        posGS.setOnClickListener(v -> assignPlayerToPosition("court_goalScore"));
        posC.setOnClickListener(v -> assignPlayerToPosition("court_centre"));
        posWA.setOnClickListener(v -> assignPlayerToPosition("court_WingAttack"));
        posWD.setOnClickListener(v -> assignPlayerToPosition("court_WingDefence"));
        posGD.setOnClickListener(v -> assignPlayerToPosition("court_Defence"));
        posGK.setOnClickListener(v -> assignPlayerToPosition("court_GoalKeeper"));

        // Fetch players
        loadPlayersFromSupabase();
    }

    private void assignPlayerToPosition(String positionField) {
        if (selectedPlayer == null)
        {
            Toast.makeText(this, "Please select a player first.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a request to send to your Supabase API
        Court assignment = new Court(positionField, currentGameId,selectedPlayer.getPlayer_ID());

        Call<List<Court>> call = api.assignPlayerToCourt(assignment);
        call.enqueue(new Callback<List<Court>>()
        {
            @Override
            public void onResponse(Call<List<Court>> call, Response<List<Court>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String playerName = selectedPlayer.getPlayer_FirstName() + " " + selectedPlayer.getPlayer_Surname();

                    switch (positionField) {
                        case "court_goalAttack":
                            posGA.setText(playerName);
                            break;
                        case "court_goalScore":
                            posGS.setText(playerName);
                            break;
                        case "court_centre":
                            posC.setText(playerName);
                            break;
                        case "court_WingAttack":
                            posWA.setText(playerName);
                            break;
                        case "court_WingDefence":
                            posWD.setText(playerName);
                            break;
                        case "court_Defence":
                            posGD.setText(playerName);
                            break;
                        case "court_GoalKeeper":
                            posGK.setText(playerName);
                            break;
                    }

                    Toast.makeText(SetUpCourtActivity.this, "Player assigned to " + positionField, Toast.LENGTH_SHORT).show();

                    // Remove player from list
                    adapter.removePlayer(selectedPlayer);
                    selectedPlayer = null;

                } else {
                    Toast.makeText(SetUpCourtActivity.this, "Assignment failed: " + response.code(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(SetUpCourtActivity.this, ""+selectedPlayer.getPlayer_ID(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<Court>> call, Throwable t) {
                Toast.makeText(SetUpCourtActivity.this, "API error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadPlayersFromSupabase() {
        Call<List<Player>> call = api.getPlayers("*");

        call.enqueue(new Callback<List<Player>>() {
            @Override
            public void onResponse(Call<List<Player>> call, Response<List<Player>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Player> playerList = response.body();
                    adapter = new PlayerAdapterCourt(playerList, SetUpCourtActivity.this, player -> {
                        selectedPlayer = player;
                        Toast.makeText(SetUpCourtActivity.this, "Selected: " + player.getPlayer_FirstName(), Toast.LENGTH_SHORT).show();
                    });
                    lstPlayers.setAdapter(adapter);

                } else
                {
                    Toast.makeText(SetUpCourtActivity.this, "Failed to load players", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Player>> call, Throwable t) {
                Toast.makeText(SetUpCourtActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onBackClicked(View view) {
        Intent intent = new Intent(SetUpCourtActivity.this, SetUpNewGameActivity.class);
        startActivity(intent);
        finish();
    }

    public void onNextClicked(View view) {
        Intent intent = new Intent(SetUpCourtActivity.this, SetBenchPlayersActivity.class);
        startActivity(intent);
        finish();
    }
}