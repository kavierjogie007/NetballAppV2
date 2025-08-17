package com.example.netballapp.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.netballapp.Model.Player;
import com.example.netballapp.Model.SessionManager;
import com.example.netballapp.adapters.PlayerAdapter;
import com.example.netballapp.R;
import com.example.netballapp.api.RetrofitClient;
import com.example.netballapp.api.SuperbaseAPI;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Player_Profiles extends AppCompatActivity {
    private SuperbaseAPI api;
    private PlayerAdapter adapter;
    private List<Player> players;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_profiles);

        ListView listView = findViewById(R.id.playerListView);

        players = new ArrayList<>();

        adapter = new PlayerAdapter(this, players, (player, position) ->
                new AlertDialog.Builder(Player_Profiles.this)
                .setTitle("Confirm Deletion")
                .setMessage("Delete " + player.getPlayer_FirstName() + "?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    deletePlayerFromAPI(player.getPlayer_ID(), position);
                })
                .setNegativeButton("Cancel", null)
                .show());
        listView.setAdapter(adapter);

        api = RetrofitClient.getClient().create(SuperbaseAPI.class);

        Call<List<Player>> call = api.getPlayers("*"); // Selects all columns
        call.enqueue(new Callback<List<Player>>() {
            @Override
            public void onResponse(Call<List<Player>> call, Response<List<Player>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    players.clear();
                    players.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else
                {
                    Log.e("API", "Unsuccessful: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<List<Player>> call, Throwable t)
            {
                Log.e("API", "Failed: " + t.getMessage());
            }
        });
    }

    private void deletePlayerFromAPI(long playerId, int position) {
        Call<Void> call = api.deletePlayer("eq." + playerId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    players.remove(position);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(Player_Profiles.this, "Player deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Player_Profiles.this, "Failed to delete player", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(Player_Profiles.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void onBackClicked(View view)
    {
        Intent intent = new Intent(Player_Profiles.this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }

    public void onAddPlayerClicked(View view) {
        Intent intent = new Intent(Player_Profiles.this, AddPlayer.class);
        startActivity(intent);
        finish();
    }

    public void onLogoutClicked(View view) {
        SessionManager.logout(this);
    }
}