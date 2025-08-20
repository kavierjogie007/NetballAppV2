package com.example.netballapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.netballapp.Model.Court;
import com.example.netballapp.Model.Player;
import com.example.netballapp.R;
import com.example.netballapp.adapters.PlayerAdapterCourt;
import com.example.netballapp.api.RetrofitClient;
import com.example.netballapp.api.SuperbaseAPI;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SetBenchPlayersActivity extends AppCompatActivity {

    private RecyclerView lstPlayers;
    private PlayerAdapterCourt adapter;
    private SuperbaseAPI api;
    private Player selectedPlayer;

    // Bench containers
    private LinearLayout attackContainer, defenceContainer, centreContainer;
    // Bench counters
    private int attackBenchCount = 0;
    private int defenceBenchCount = 0;
    private int centreBenchCount = 0;
    private int totalBenchCount = 0;
    private static final int MAX_TOTAL_BENCH = 4;
    private long currentGameId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_bench_players);


        lstPlayers = findViewById(R.id.lstPlayers);
        attackContainer = findViewById(R.id.attackContainer);
        defenceContainer = findViewById(R.id.defenceContainer);
        centreContainer = findViewById(R.id.centreContainer);

        lstPlayers.setLayoutManager(new LinearLayoutManager(this));

        api = RetrofitClient.getClient().create(SuperbaseAPI.class);

        // Get current game ID
        currentGameId = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                .getLong("game_ID", -1);
        if (currentGameId == -1) {
            Toast.makeText(this, "Game ID not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        List<Player> playerList = (List<Player>) getIntent().getSerializableExtra("players_list");
        adapter = new PlayerAdapterCourt(playerList, this, player -> {
            selectedPlayer = player;
            Toast.makeText(this, "Selected: " + player.getPlayer_FirstName(), Toast.LENGTH_SHORT).show();
        });
        lstPlayers.setAdapter(adapter);


        // Set click listeners with section names
        attackContainer.setOnClickListener(v -> assignPlayerToBench(attackContainer, "ATTACK"));
        defenceContainer.setOnClickListener(v -> assignPlayerToBench(defenceContainer, "DEFENCE"));
        centreContainer.setOnClickListener(v -> assignPlayerToBench(centreContainer, "CENTRE"));
    }

    private void assignPlayerToBench(LinearLayout container, String section) {
        if (selectedPlayer == null) {
            Toast.makeText(this, "Please select a player first.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check global bench limit
        if (totalBenchCount >= MAX_TOTAL_BENCH) {
            Toast.makeText(this, "All bench positions are full!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Increment section and global counters
        switch (section) {
            case "ATTACK": attackBenchCount++; break;
            case "DEFENCE": defenceBenchCount++; break;
            case "CENTRE": centreBenchCount++; break;
        }
        totalBenchCount++;

        // Create bench position string
        String benchPosition = section + "_" + (section.equals("ATTACK") ? attackBenchCount :
                section.equals("DEFENCE") ? defenceBenchCount : centreBenchCount);

        // Create Court object to save in database
        Court assignment = new Court(benchPosition, currentGameId, selectedPlayer.getPlayer_ID());

        Call<List<Court>> call = api.assignPlayerToCourt(assignment);
        call.enqueue(new Callback<List<Court>>() {
            @Override
            public void onResponse(Call<List<Court>> call, Response<List<Court>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Add player to UI
                    TextView playerView = new TextView(SetBenchPlayersActivity.this);
                    playerView.setText(selectedPlayer.getPlayer_FirstName() + " " + selectedPlayer.getPlayer_Surname());
                    playerView.setTextSize(16);
                    playerView.setPadding(8, 8, 8, 8);
                    playerView.setBackgroundColor(Color.parseColor("#cccccc"));
                    playerView.setTextColor(Color.BLACK);
                    container.addView(playerView);

                    Toast.makeText(SetBenchPlayersActivity.this,
                            "Player assigned to " + benchPosition, Toast.LENGTH_SHORT).show();

                    adapter.removePlayer(selectedPlayer);
                    selectedPlayer = null;
                } else {
                    Toast.makeText(SetBenchPlayersActivity.this, "Assignment failed: " + response.code(), Toast.LENGTH_SHORT).show();
                    // Roll back counters if failed
                    switch (section) {
                        case "ATTACK": attackBenchCount--; break;
                        case "DEFENCE": defenceBenchCount--; break;
                        case "CENTRE": centreBenchCount--; break;
                    }
                    totalBenchCount--;
                }
            }

            @Override
            public void onFailure(Call<List<Court>> call, Throwable t) {
                Toast.makeText(SetBenchPlayersActivity.this, "API error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                // Roll back counters if failed
                switch (section) {
                    case "ATTACK": attackBenchCount--; break;
                    case "DEFENCE": defenceBenchCount--; break;
                    case "CENTRE": centreBenchCount--; break;
                }
                totalBenchCount--;
            }
        });
    }


    public void onBackClicked(android.view.View view) {
        Intent intent = new Intent(SetBenchPlayersActivity.this, SetUpCourtActivity.class);
        startActivity(intent);
        finish();
    }

    public void onStartGameClicked(View view) {
        Intent intent = new Intent(SetBenchPlayersActivity.this, SetBenchPlayersActivity.class);
        startActivity(intent);
        finish();
    }
}
