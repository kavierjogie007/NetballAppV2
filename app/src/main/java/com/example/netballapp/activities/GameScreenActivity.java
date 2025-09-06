package com.example.netballapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.netballapp.Model.Court;
import com.example.netballapp.Model.Game;
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

    private TextView timerText, scoreMadibaz, scoreOpposition,scoreMadibazName,scoreOppositionName;
    private Button startButton, endHalfButton,addOppositionScoreButton;
    private CountDownTimer countUpTimer;
    private TextView centrePassText;
    private String currentCentrePassTeam; // stores "Madibaz" or opposition
    private EditText coachNotes;

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
        startButton = findViewById(R.id.startButton);
        endHalfButton = findViewById(R.id.endHalfButton);
        centrePassText = findViewById(R.id.centrePassText);
        scoreMadibaz = findViewById(R.id.scoreMadibazValue);      // only value
        scoreOpposition = findViewById(R.id.scoreOppositionValue); // only value
        scoreMadibazName = findViewById(R.id.scoreMadibaz);   // team name
        scoreOppositionName = findViewById(R.id.scoreOpposition); // team name
        addOppositionScoreButton = findViewById(R.id.addOppositionScoreButton);
        coachNotes = findViewById(R.id.coachNotes);

        // Set team names
        scoreMadibazName.setText("Madibaz");
        oppositionName = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                .getString("oppositionName", "Opposition");
        scoreOppositionName.setText(oppositionName);

        // Disable score button until start
        addOppositionScoreButton.setEnabled(false);

        // Disable all player positions
        disableAllPlayerPositions();


        // Initialize scores
        scoreMadibaz.setText("0");
        scoreOpposition.setText("0");

        // ====== INIT TIMER ======
        updateTimerText();

        startButton.setOnClickListener(v -> {
            if (!isTimerRunning) {
                showCentrePassDialog();
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

        addOppositionScoreButton.setOnClickListener(v -> {
            if (!isTimerRunning) {
                Toast.makeText(GameScreenActivity.this, "Start the game first!", Toast.LENGTH_SHORT).show();
                return;
            }
            int currentScore = Integer.parseInt(scoreOpposition.getText().toString());
            currentScore++;
            scoreOpposition.setText(String.valueOf(currentScore));

            // Toggle centre pass
            if (oppositionName.equals(currentCentrePassTeam)) {
                currentCentrePassTeam = "Madibaz";
            } else {
                currentCentrePassTeam = oppositionName;
            }
            centrePassText.setText("Centre Pass: " + currentCentrePassTeam);

            // Update DB
            Map<String, Object> updates = new java.util.HashMap<>();
            updates.put("game_opposition_score", currentScore);
            updates.put("game_current_centre_pass_team", currentCentrePassTeam);

            Call<List<Game>> call = api.updateGameScore("eq." + gameId, updates);
            call.enqueue(new Callback<List<Game>>() {
                @Override
                public void onResponse(Call<List<Game>> call, Response<List<Game>> response) {
                    if (!response.isSuccessful()) {
                        Toast.makeText(GameScreenActivity.this,
                                "Failed to update opposition score: " + response.code(),
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Game>> call, Throwable t) {
                    Toast.makeText(GameScreenActivity.this,
                            "Network error: " + t.getLocalizedMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        });

    }

    private void showCentrePassDialog() {
        String[] options = {"Madibaz", oppositionName};

        new android.app.AlertDialog.Builder(this)
                .setTitle("Set First Centre Pass")
                .setItems(options, (dialog, which) -> {
                    currentCentrePassTeam = options[which];
                    centrePassText.setText("Centre Pass: " + currentCentrePassTeam);
                    Toast.makeText(this, "First centre pass: " + currentCentrePassTeam, Toast.LENGTH_SHORT).show();
                    startTimer();
                    addOppositionScoreButton.setEnabled(true);
                    enableAllPlayerPositions();
                    startButton.setEnabled(false); // disables button click
                    startButton.setAlpha(0.5f);    // visually grey it out


                })
                .setCancelable(false)
                .show();
    }

    private void updateScoreAndCentrePass() {
        // 1️⃣ Update Madibaz score locally
        int currentScore = Integer.parseInt(scoreMadibaz.getText().toString());
        currentScore++;
        scoreMadibaz.setText(String.valueOf(currentScore));

        // 2️⃣ Toggle centre pass locally
        if ("Madibaz".equals(currentCentrePassTeam)) {
            currentCentrePassTeam = oppositionName;
        } else {
            currentCentrePassTeam = "Madibaz";
        }
        centrePassText.setText("Centre Pass: " + currentCentrePassTeam);

        // 3️⃣ Update DB in a single call
        Long gameId = getSharedPreferences("MyAppPrefs", MODE_PRIVATE).getLong("game_ID", -1);
        Map<String, Object> updates = new java.util.HashMap<>();
        updates.put("game_madibaz_score", currentScore);
        updates.put("game_current_centre_pass_team", currentCentrePassTeam);

        Call<List<Game>> call = api.updateGameScore("eq." + gameId, updates);
        call.enqueue(new Callback<List<Game>>() {
            @Override
            public void onResponse(Call<List<Game>> call, Response<List<Game>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(GameScreenActivity.this,
                            "Failed to update score/centre pass: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Game>> call, Throwable t) {
                Toast.makeText(GameScreenActivity.this,
                        "Network error: " + t.getLocalizedMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
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
        saveCoachNoteOnHalfEnd(); // <-- Save notes first

        if (currentHalf == 1) {
            Toast.makeText(this, "End of First Half", Toast.LENGTH_SHORT).show();
            currentHalf = 2;
            resetTimer();
            TextView halfLabel = findViewById(R.id.halfLabel);
            halfLabel.setText("2nd Half");

            // Re-enable start button for second half
            startButton.setEnabled(true);
            startButton.setAlpha(1.0f);
        } else if (currentHalf == 2) {
            Toast.makeText(this, "Full Time", Toast.LENGTH_LONG).show();
            stopTimer();

            Intent intent = new Intent(GameScreenActivity.this, MatchAnalysis.class);
            startActivity(intent);
            finish();
        }
    }


    private void resetTimer() {
        elapsedTime = 0;
        updateTimerText();
    }

    private void updateTimerText() {
        int minutes = (int) (elapsedTime / 1000) / 60;
        int seconds = (int) (elapsedTime / 1000) % 60;

        String halfLabelText = currentHalf == 1 ? "1st Half" : "2nd Half";

        timerText.setText(String.format("%02d:%02d", minutes, seconds));
        TextView halfLabel = findViewById(R.id.halfLabel);
        halfLabel.setText(halfLabelText);
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
                            if (!isTimerRunning) {
                                Toast.makeText(GameScreenActivity.this, "Start the game first!", Toast.LENGTH_SHORT).show();
                                return;
                            }
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

    private void saveCoachNoteOnHalfEnd() {
        String note = coachNotes.getText().toString().trim();
        if (note.isEmpty()) return; // skip if empty

        Long gameId = getSharedPreferences("MyAppPrefs", MODE_PRIVATE).getLong("game_ID", -1);
        if (gameId == -1) return;

        Map<String, Object> updates = new java.util.HashMap<>();
        updates.put("game_coach_note", note);

        Call<List<Game>> call = api.updateGameScore("eq." + gameId, updates);
        call.enqueue(new Callback<List<Game>>() {
            @Override
            public void onResponse(Call<List<Game>> call, Response<List<Game>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(GameScreenActivity.this, "Failed to save coach note: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Game>> call, Throwable t) {
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

                    if (actionType.equals("Goal") || actionType.equals("Penalty Goal")) {
                        updateScoreAndCentrePass(); // updates both score & centre pass in UI and DB
                    }

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
        call.enqueue(new Callback<Void>()
        {
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

    private void disableAllPlayerPositions() {
        String[] positions = {"GS", "GA", "WA", "C", "WD", "GD", "GK"}; // all possible positions
        for (String pos : positions) {
            int resId = getResources().getIdentifier("pos" + pos, "id", getPackageName());
            TextView posText = findViewById(resId);
            if (posText != null) {
                posText.setClickable(false);
                posText.setAlpha(0.5f); // optionally grey out
            }
        }
    }

    private void enableAllPlayerPositions() {
        String[] positions = {"GS", "GA", "WA", "C", "WD", "GD", "GK"}; // all possible positions
        for (String pos : positions) {
            int resId = getResources().getIdentifier("pos" + pos, "id", getPackageName());
            TextView posText = findViewById(resId);
            if (posText != null) {
                posText.setClickable(true);
                posText.setAlpha(1.0f); // reset opacity
            }
        }
    }
}
