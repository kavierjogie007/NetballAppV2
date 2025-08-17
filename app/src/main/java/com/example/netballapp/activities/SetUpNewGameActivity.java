package com.example.netballapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.netballapp.Model.Game;
import com.example.netballapp.Model.UIUtils;
import com.example.netballapp.R;
import com.example.netballapp.api.RetrofitClient;
import com.example.netballapp.api.SuperbaseAPI;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SetUpNewGameActivity extends AppCompatActivity {
    private EditText edtGameName, edtOppositionName, edtGameVenue, edtGameDate;
    private AutoCompleteTextView actvGameType;
    private SuperbaseAPI api;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_new_game);

        edtGameName = findViewById(R.id.edtGameName);
        edtOppositionName = findViewById(R.id.edtOpposition);
        edtGameVenue = findViewById(R.id.edtVenue);
        edtGameDate = findViewById(R.id.edtGameDate);
        actvGameType=findViewById(R.id.actvGameType);

        String[] gameTypes = {"Friendly", "League", "Tournament"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.dropdown_item, gameTypes);
        actvGameType.setAdapter(adapter);

        api = RetrofitClient.getClient().create(SuperbaseAPI.class);
    }

    public void onBackClicked(View view) {
        Intent intent = new Intent(SetUpNewGameActivity.this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }

    public void onAddGameToDBClicked(View view) {
        String gameName = edtGameName.getText().toString().trim();
        String oppositionName= edtOppositionName.getText().toString().trim();
        String gameDate=edtGameDate.getText().toString().trim();
        String gameVenue=edtGameVenue.getText().toString().trim();
        String gameType = actvGameType.getText().toString().trim();


        if (gameName.isEmpty() || oppositionName.isEmpty() || gameVenue.isEmpty() || gameDate.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Game game = new Game(gameName,oppositionName,gameVenue,gameDate,gameType,0,0,0,0,"","");

        Call<List<Game>> call = api.setUpNewGame(game);
        call.enqueue(new Callback<List<Game>>() {
            @Override
            public void onResponse(Call<List<Game>> call, Response<List<Game>> response) {
                if (response.isSuccessful()) {
                    Game savedGame=response.body().get(0);
                    Toast.makeText(SetUpNewGameActivity.this, "Set Up New Game successful!", Toast.LENGTH_SHORT).show();

                    // Saves coach ID
                    getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                            .edit()
                            .putLong("game_ID", savedGame.getGame_ID())
                            .putInt("game_BenchPlayers",savedGame.getGame_BenchPositions())
                            .apply();

                    Intent intent = new Intent(SetUpNewGameActivity.this, SetUpCourtActivity.class);
                    startActivity(intent);
                   finish();
                }
                else
                {
                    Toast.makeText(SetUpNewGameActivity.this, "Set Up Game failed.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(SetUpNewGameActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e("API_ERROR", "Error Body: " + errorBody);
                            Toast.makeText(SetUpNewGameActivity.this, "Error: " + errorBody, Toast.LENGTH_LONG).show();
                        } else {
                            Log.e("API_ERROR", "No error body returned");
                        }
                    } catch (IOException e) {
                        Log.e("API_ERROR", "IOException while reading error body", e);
                    }

                }
            }
            @Override
            public void onFailure(Call<List<Game>> call, Throwable t) {
                Toast.makeText(SetUpNewGameActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onGameDateClicked(View view) {
        UIUtils.showFutureDatePicker(this, edtGameDate);
    }
}