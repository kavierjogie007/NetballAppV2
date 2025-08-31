package com.example.netballapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.netballapp.Model.Game;
import com.example.netballapp.Model.Player;
import com.example.netballapp.Model.PlayerAction;
import com.example.netballapp.Model.PlayerStatsView;
import com.example.netballapp.R;
import com.example.netballapp.adapters.PlayerStatsAdapter;
import com.example.netballapp.api.RetrofitClient;
import com.example.netballapp.api.SuperbaseAPI;
import com.google.android.material.card.MaterialCardView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MatchAnalysis extends AppCompatActivity {

    private TextView tvMadibazScore, tvOppositionScore, tvGameName, tvGameDateVenue;
    private RecyclerView rvPlayerActions;
    private TextView tvTeamStats;
    private RadioGroup toggleStats;
    private SuperbaseAPI api;
    private MaterialCardView teamStatsCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_analysis);

        tvGameName = findViewById(R.id.tvGameName);
        tvGameDateVenue = findViewById(R.id.tvGameDateVenue);
        tvMadibazScore = findViewById(R.id.tvMadibazScore);
        tvOppositionScore = findViewById(R.id.tvOppositionScore);
        rvPlayerActions = findViewById(R.id.rvPlayerActions);
        tvTeamStats = findViewById(R.id.tvTeamStats);
        toggleStats = findViewById(R.id.toggleStats);
        teamStatsCard = findViewById(R.id.teamStatsCard);

        rvPlayerActions.setLayoutManager(new LinearLayoutManager(this));

        toggleStats.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbPlayerPerformance) {
                rvPlayerActions.setVisibility(View.VISIBLE);
                teamStatsCard.setVisibility(View.GONE);
            } else {
                rvPlayerActions.setVisibility(View.GONE);
                teamStatsCard.setVisibility(View.VISIBLE);
            }
        });

        api = RetrofitClient.getClient().create(SuperbaseAPI.class);

        long gameID = getSharedPreferences("MyAppPrefs", MODE_PRIVATE).getLong("game_ID", -1);

        if (gameID != -1) {
            loadGameData(gameID);
        } else {
            Toast.makeText(this, "Game ID not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadPlayerStats(long gameID) {
        api.getPlayerStatsByGame("eq." + gameID).enqueue(new Callback<List<PlayerStatsView>>() {
            @Override
            public void onResponse(Call<List<PlayerStatsView>> call, Response<List<PlayerStatsView>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PlayerStatsView> statsList = response.body();

                    // Show player stats in RecyclerView
                    rvPlayerActions.setAdapter(new PlayerStatsAdapter(statsList));

                    // Calculate and show team totals in TextView
                    calculateAndDisplayTeamStats(statsList);
                } else {
                    Toast.makeText(MatchAnalysis.this, "Failed to fetch player stats", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PlayerStatsView>> call, Throwable t) {
                Toast.makeText(MatchAnalysis.this, "Error fetching stats: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadGameData(long gameID) {
        api.getGameById("eq." + gameID).enqueue(new Callback<List<Game>>() {
            @Override
            public void onResponse(Call<List<Game>> call, Response<List<Game>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Game game = response.body().get(0);

                    tvGameName.setText(game.getGame_Name());
                    tvGameDateVenue.setText(game.getGame_Date() + " | " + game.getGame_Venue());
                    tvMadibazScore.setText("Madibaz: " + game.getGame_MadibazScore());
                    tvOppositionScore.setText("Team B: " + game.getGame_OppositionScore());

                    loadPlayerStats(gameID);
                } else {
                    Toast.makeText(MatchAnalysis.this, "Failed to fetch game info", Toast.LENGTH_SHORT).show();
                    // log error
                    if (response.errorBody() != null) {
                        try { System.out.println("Error: " + response.errorBody().string()); } catch (Exception e) {}
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Game>> call, Throwable t) {
                Toast.makeText(MatchAnalysis.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void calculateAndDisplayTeamStats(List<PlayerStatsView> playerStats) {
        // Manually calculate totals here (since adapter no longer does it)
        PlayerStatsView totals = new PlayerStatsView();

        for (PlayerStatsView stats : playerStats) {
            totals.setGoal(totals.getGoal() + stats.getGoal());
            totals.setPenalty_goal(totals.getPenalty_goal() + stats.getPenalty_goal());
            totals.setGoal_missed(totals.getGoal_missed() + stats.getGoal_missed());
            totals.setFor_(totals.getFor_() + stats.getFor_());
            totals.setAgainst(totals.getAgainst() + stats.getAgainst());
            totals.setOffensive_rebound(totals.getOffensive_rebound() + stats.getOffensive_rebound());
            totals.setDefensive_rebound(totals.getDefensive_rebound() + stats.getDefensive_rebound());
            totals.setCentre_pass_receive(totals.getCentre_pass_receive() + stats.getCentre_pass_receive());
            totals.setGoal_assist(totals.getGoal_assist() + stats.getGoal_assist());
            totals.setDeflection(totals.getDeflection() + stats.getDeflection());
            totals.setIntercept(totals.getIntercept() + stats.getIntercept());
            totals.setDrop_ball(totals.getDrop_ball() + stats.getDrop_ball());
            totals.setHeld_ball(totals.getHeld_ball() + stats.getHeld_ball());
            totals.setStepping(totals.getStepping() + stats.getStepping());
            totals.setBreak_(totals.getBreak_() + stats.getBreak_());
            totals.setContact(totals.getContact() + stats.getContact());
            totals.setObstruction(totals.getObstruction() + stats.getObstruction());
        }

        int totalShots = totals.getGoal() + totals.getPenalty_goal() + totals.getGoal_missed();
        double successRate = totalShots > 0 ? ((double) (totals.getGoal() + totals.getPenalty_goal()) / totalShots) * 100 : 0;

        String teamStatsText = "=== Team Totals ===\n\n" +
                "Goals: " + totals.getGoal() + "\n" +
                "Penalty Goals: " + totals.getPenalty_goal() + "\n" +
                "Goals Missed: " + totals.getGoal_missed() + "\n" +
                String.format("Success Rate: %.1f%%\n\n", successRate) +
                "Positive Play:\n" +
                "For: " + totals.getFor_() + "\n" +
                "Against: " + totals.getAgainst() + "\n" +
                "Offensive Rebound: " + totals.getOffensive_rebound() + "\n" +
                "Defensive Rebound: " + totals.getDefensive_rebound() + "\n" +
                "Centre Pass Receive: " + totals.getCentre_pass_receive() + "\n" +
                "Goal Assist: " + totals.getGoal_assist() + "\n" +
                "Deflection: " + totals.getDeflection() + "\n" +
                "Intercept: " + totals.getIntercept() + "\n\n" +
                "Penalties & Errors:\n" +
                "Drop Ball: " + totals.getDrop_ball() + "\n" +
                "Held Ball: " + totals.getHeld_ball() + "\n" +
                "Stepping: " + totals.getStepping() + "\n" +
                "Break: " + totals.getBreak_() + "\n" +
                "Contact: " + totals.getContact() + "\n" +
                "Obstruction: " + totals.getObstruction();

        tvTeamStats.setText(teamStatsText);

        // make sure it's visible if "Team Statistics" is selected
        if (toggleStats.getCheckedRadioButtonId() == R.id.rbTeamStats) {
            rvPlayerActions.setVisibility(View.GONE);
            teamStatsCard.setVisibility(View.VISIBLE);
        }

    }



}
