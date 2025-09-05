package com.example.netballapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.netballapp.Model.Court;
import com.example.netballapp.Model.Player;
import com.example.netballapp.Model.PlayerAction;
import com.example.netballapp.R;
import com.example.netballapp.api.RetrofitClient;
import com.example.netballapp.api.SuperbaseAPI;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GameScreenActivity extends AppCompatActivity {

    private TextView timerText, scoreMadibaz, scoreOpposition;
    private Button startButton, endHalfButton;
    private CountDownTimer countUpTimer;
    private boolean isTimerRunning = false;
    // ====== Half duration (30 minutes) ======
    private static final long HALF_TIME_MILLIS = 30 * 60 * 1000;
    // Timer state
    private long elapsedTime = 0; // will count UP
    private int currentHalf = 1;  // 1 = first half, 2 = second half

    private String oppositionName = "Opposition"; // default
    private final java.util.Map<Long, java.util.Map<String, Integer>> playerStats = new java.util.HashMap<>();

    private final java.util.Map<Long, java.util.List<PlayerAction>> playerActionHistory = new java.util.HashMap<>();
    private SuperbaseAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);

        // ====== FIND VIEWS ======
        timerText = findViewById(R.id.timer);
        scoreMadibaz = findViewById(R.id.scoreMadibaz);
        scoreOpposition = findViewById(R.id.scoreThunderbolts);
        startButton = findViewById(R.id.startButton);
        endHalfButton = findViewById(R.id.endHalfButton);

        // ====== TEAM NAMES ======
        scoreMadibaz.setText("Madibaz: 0");

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("oppositionName")) {
            oppositionName = intent.getStringExtra("oppositionName");
        }
        scoreOpposition.setText(oppositionName + ": 0");

        // ====== INIT TIMER ======
        updateTimerText();

        startButton.setOnClickListener(v -> {
            if (!isTimerRunning) {
                startTimer();
            }
        });

        endHalfButton.setOnClickListener(v -> {
            stopTimer();
            handleEndHalf();
        });

        api = RetrofitClient.getClient().create(SuperbaseAPI.class);

        // Load players from DB for this game
        // Retrieves coach ID from SharedPreferences
        Long gameId = getSharedPreferences("MyAppPrefs", MODE_PRIVATE).getLong("game_ID", -1);

        Toast.makeText(this, "Game ID: " + gameId, Toast.LENGTH_SHORT).show();

        loadPlayers(gameId);
    }

    // ---------- TIMER METHODS ----------
    private void startTimer() {
        countUpTimer = new CountDownTimer(HALF_TIME_MILLIS - elapsedTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                elapsedTime += 1000; // count UP
                updateTimerText();
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                handleEndHalf();
            }
        }.start();

        isTimerRunning = true;
    }

    private void stopTimer() {
        if (countUpTimer != null) {
            countUpTimer.cancel();
        }
        isTimerRunning = false;
    }

    private void handleEndHalf() {
        if (currentHalf == 1) {
            Toast.makeText(this, "End of First Half", Toast.LENGTH_SHORT).show();
            currentHalf = 2;
            resetTimer();
        } else if (currentHalf == 2) {
            Toast.makeText(this, "Full Time", Toast.LENGTH_LONG).show();
            stopTimer();

            // ----- Navigate to Results Activity -----
            Intent intent = new Intent(GameScreenActivity.this, MatchAnalysis.class);

            startActivity(intent);
            finish(); // optional, so user can't go back to game screen
        }
    }

    private void resetTimer() {
        elapsedTime = 0;
        updateTimerText();
    }

    private void updateTimerText() {
        int minutes = (int) (elapsedTime / 1000) / 60;
        int seconds = (int) (elapsedTime / 1000) % 60;

        String halfIndicator = currentHalf + "/2";
        String timeFormatted = String.format("%02d:%02d (%s)", minutes, seconds, halfIndicator);
        timerText.setText(timeFormatted);
    }

    private void loadPlayers(Long gameId) {
        Call<List<Court>> call = api.getCourtAssignments("eq." + gameId);

        call.enqueue(new Callback<List<Court>>() {
            @Override
            public void onResponse(Call<List<Court>> call, Response<List<Court>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(GameScreenActivity.this, "Server error: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

                List<Court> courtAssignments = response.body();
                if (courtAssignments != null) {
                    for (Court court : courtAssignments) {
                        Long playerId = court.getPlayer_id();
                        String pos = court.getCourt_position_field();

                        // Now fetch player details by ID
                        loadPlayerDetails(playerId, pos);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Court>> call, Throwable t) {
                Toast.makeText(GameScreenActivity.this, "Network error: " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPlayerDetails(Long playerId, String pos) {
        Call<List<Player>> call = api.getPlayerById("eq." + playerId);
        call.enqueue(new Callback<List<Player>>() {
            @Override
            public void onResponse(Call<List<Player>> call, Response<List<Player>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(GameScreenActivity.this, "Error loading player: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

                List<Player> players = response.body();
                if (players != null && !players.isEmpty()) {
                    Player player = players.get(0);

                    // Create two-line display with initials and position
                    String playerDisplay = getInitials(player) + "\n(" + pos + ")";

                    // Match "posGS", "posGA", etc. in layout
                    int resId = getResources().getIdentifier("pos" + pos, "id", getPackageName());
                    TextView posText = findViewById(resId);
                    if (posText != null) {
                        posText.setText(playerDisplay);

                        // Add click listener to record an action
                        posText.setOnClickListener(v -> {
                            showActionDialog(player, pos);
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Player>> call, Throwable t) {
                Toast.makeText(GameScreenActivity.this, "Network error: " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Helper method
    private String getInitials(Player player) {
        String firstInitial = player.getPlayer_FirstName().substring(0, 1).toUpperCase();
        String lastInitial = player.getPlayer_Surname().substring(0, 1).toUpperCase();
        return firstInitial + "." + lastInitial + ".";
    }

    private void showActionDialog(Player player, String pos) {
        String[] actions = {
                "Goal",
                "Penalty Goal",
                "Goal Missed",
                "For",
                "Against",
                "Drop Ball",
                "Held Ball",
                "Stepping",
                "Break",
                "Contact",
                "Obstruction",
                "Centre Pass Receive",
                "Goal Assist",
                "Offensive Rebound",
                "Defensive Rebound",
                "Deflection",
                "Intercept"
        };

        // Get current stats for this player
        Map<String, Integer> stats = playerStats.get(player.getPlayer_ID());

        // Build display array with totals
        String[] actionsWithTotals = new String[actions.length];
        for (int i = 0; i < actions.length; i++) {
            int count = 0;
            if (stats != null) {
                count = stats.getOrDefault(actions[i], 0);
            }
            actionsWithTotals[i] = actions[i] + " (" + count + ")";
        }

        new android.app.AlertDialog.Builder(this)
                .setTitle("Record Action for " + player.getPlayer_FirstName() + " " + player.getPlayer_Surname())
                .setItems(actionsWithTotals, (dialog, which) -> {
                    String actionType = actions[which]; // exact action type

                    String timestamp = timerText.getText().toString().split(" ")[0];
                    String period = "Half " + currentHalf;
                    Long gameId = getSharedPreferences("MyAppPrefs", MODE_PRIVATE).getLong("game_ID", -1);

                    PlayerAction action = new PlayerAction(
                            actionType,
                            timestamp,
                            period,
                            player.getPlayer_ID(),
                            gameId
                    );

                    // Update local totals & history
                    updatePlayerStats(action);

                    playerActionHistory.putIfAbsent(player.getPlayer_ID(), new java.util.ArrayList<>());
                    playerActionHistory.get(player.getPlayer_ID()).add(action);

                    // Save to API
                    savePlayerAction(action);

                    // Reopen dialog to refresh totals
                    showActionDialog(player, pos);
                })
                .setNegativeButton("Close", null)
                .show();
    }

    private void updatePlayerStats(PlayerAction action) {
        playerStats.putIfAbsent(action.getPlayer_ID(), new java.util.HashMap<>());
        Map<String, Integer> stats = playerStats.get(action.getPlayer_ID());

        int currentCount = stats.getOrDefault(action.getAction_Type(), 0);
        stats.put(action.getAction_Type(), currentCount + 1);
    }

    private void savePlayerAction(PlayerAction action) {
        Call<Void> call = api.recordPlayerAction(action);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(GameScreenActivity.this, "Action recorded!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(GameScreenActivity.this, "Error saving action: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(GameScreenActivity.this, "Network error: " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
