package com.example.netballapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Player_Profiles extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_profiles);

        ListView listView = findViewById(R.id.playerListView);

        List<Player> players = new ArrayList<>();
        PlayerAdapter adapter = new PlayerAdapter(this, players);
        listView.setAdapter(adapter);

        SuperbaseAPI api = RetrofitClient.getClient().create(SuperbaseAPI.class);

        Call<List<Player>> call = api.getPlayers("*"); // Select all columns
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
    public void onBackClicked(View view)
    {
        Intent intent = new Intent(Player_Profiles.this, DashboardActivity.class);
        startActivity(intent);
        finish(); // Finish LoginActivity so it's not in the back stack
    }

    public void onAddPlayerClicked(View view) {
        Intent intent = new Intent(Player_Profiles.this, AddPlayer.class);
        startActivity(intent);
        finish(); // Finish LoginActivity so it's not in the back stack
    }
}