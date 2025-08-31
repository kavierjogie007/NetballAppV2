package com.example.netballapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
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
    private com.github.mikephil.charting.charts.BarChart playerStatsChart;
    private MaterialCardView playerStatsChartCard;
    private  BarChart shootingAccuracyChart;
    private BarChart errorsChart;
    private RadarChart contributionsChart;
    private PieChart teamShootingChart;
    private MaterialCardView chartButtonsCard;

    private MaterialCardView rvPlayerActionsCard;

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
        playerStatsChart = findViewById(R.id.playerStatsChart);
        playerStatsChartCard = findViewById(R.id.playerStatsChartCard);
        shootingAccuracyChart = findViewById(R.id.shootingAccuracyChart);
        errorsChart = findViewById(R.id.errorsChart);
        contributionsChart = findViewById(R.id.contributionsChart);
        teamShootingChart=findViewById(R.id.teamShootingChart);
        chartButtonsCard = findViewById(R.id.chartButtonsCard);
        rvPlayerActionsCard = findViewById(R.id.rvPlayerActionsCard);

        rvPlayerActions.setLayoutManager(new LinearLayoutManager(this));

        Button btnGoalsChart = findViewById(R.id.btnGoalsChart);
        Button btnAccuracyChart = findViewById(R.id.btnAccuracyChart);
        Button btnErrorsChart = findViewById(R.id.btnErrorsChart);
        Button btnRadarChart = findViewById(R.id.btnRadarChart);
        Button btnTeamShootingChart = findViewById(R.id.btnTeamShootingChart);

        MaterialCardView goalsCard = findViewById(R.id.playerStatsChartCard);
        MaterialCardView accuracyCard = findViewById(R.id.shootingAccuracyChartCard);
        MaterialCardView errorsCard = findViewById(R.id.errorsChartCard);
        MaterialCardView radarCard = findViewById(R.id.contributionsChartCard);
        MaterialCardView teamShootingCard = findViewById(R.id.teamShootingChartCard);

        View.OnClickListener chartToggleListener = v -> {
            // Hide all chart cards first
            goalsCard.setVisibility(View.GONE);
            accuracyCard.setVisibility(View.GONE);
            errorsCard.setVisibility(View.GONE);
            radarCard.setVisibility(View.GONE);
            teamShootingCard.setVisibility(View.GONE);

            // Show the selected one
            int id = v.getId();
            if (id == R.id.btnGoalsChart) {
                goalsCard.setVisibility(View.VISIBLE);
            } else if (id == R.id.btnAccuracyChart) {
                accuracyCard.setVisibility(View.VISIBLE);
            } else if (id == R.id.btnErrorsChart) {
                errorsCard.setVisibility(View.VISIBLE);
            } else if (id == R.id.btnRadarChart) {
                radarCard.setVisibility(View.VISIBLE);
            } else if (id == R.id.btnTeamShootingChart) {
                teamShootingCard.setVisibility(View.VISIBLE);
            }
        };


        btnGoalsChart.setOnClickListener(chartToggleListener);
        btnAccuracyChart.setOnClickListener(chartToggleListener);
        btnErrorsChart.setOnClickListener(chartToggleListener);
        btnRadarChart.setOnClickListener(chartToggleListener);
        btnTeamShootingChart.setOnClickListener(chartToggleListener);


        toggleStats.setOnCheckedChangeListener((group, checkedId) -> {
            // First, hide everything
            rvPlayerActionsCard.setVisibility(View.GONE);
            teamStatsCard.setVisibility(View.GONE);
            chartButtonsCard.setVisibility(View.GONE);
            playerStatsChartCard.setVisibility(View.GONE);
            teamShootingCard.setVisibility(View.GONE);
            errorsCard.setVisibility(View.GONE);
            radarCard.setVisibility(View.GONE);
            accuracyCard.setVisibility(View.GONE);

            // Show the relevant views based on selection
            if (checkedId == R.id.rbPlayerPerformance) {
                // Show RecyclerView and chart buttons
                rvPlayerActionsCard.setVisibility(View.VISIBLE);
                chartButtonsCard.setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.rbTeamStats) {
                // Show team stats only
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
                    rvPlayerActions.setAdapter(new PlayerStatsAdapter(statsList));

                    PlayerStatsView totals = calculateTeamTotals(statsList);
                    displayTeamShootingChart(totals);

                    displayPlayerStatsChart(statsList);
                    displayShootingAccuracyChart(statsList);
                    displayErrorsChart(statsList);
                    if (!statsList.isEmpty()) displayContributionsChart(statsList.get(0));

                    // ADD THIS LINE
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

    // Move your existing calculation to return totals:
    private PlayerStatsView calculateTeamTotals(List<PlayerStatsView> statsList) {
        PlayerStatsView totals = new PlayerStatsView();
        for (PlayerStatsView stats : statsList) {
            totals.setGoal(totals.getGoal() + stats.getGoal());
            totals.setPenalty_goal(totals.getPenalty_goal() + stats.getPenalty_goal());
            totals.setGoal_missed(totals.getGoal_missed() + stats.getGoal_missed());
            // ...add other stats if needed
        }
        return totals;
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
                        try {
                            System.out.println("Error: " + response.errorBody().string());
                        } catch (Exception e) {
                        }
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

    private void displayPlayerStatsChart(List<PlayerStatsView> statsList) {
        List<com.github.mikephil.charting.data.BarEntry> entries = new ArrayList<>();
        List<String> playerNames = new ArrayList<>();

        int index = 0;
        for (PlayerStatsView stats : statsList) {
            // Example: show goals per player
            entries.add(new com.github.mikephil.charting.data.BarEntry(index, stats.getGoal()));
            playerNames.add(stats.getPlayer_name()); // make sure your PlayerStatsView has a getPlayer_Name()
            index++;
        }

        com.github.mikephil.charting.data.BarDataSet dataSet =
                new com.github.mikephil.charting.data.BarDataSet(entries, "Goals per Player");
        dataSet.setColors(com.github.mikephil.charting.utils.ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f);

        com.github.mikephil.charting.data.BarData barData = new com.github.mikephil.charting.data.BarData(dataSet);
        playerStatsChart.setData(barData);

        com.github.mikephil.charting.formatter.IndexAxisValueFormatter formatter =
                new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(playerNames);
        playerStatsChart.getXAxis().setValueFormatter(formatter);
        playerStatsChart.getXAxis().setGranularity(1f);
        playerStatsChart.getXAxis().setPosition(com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM);
        playerStatsChart.getAxisRight().setEnabled(false);

        playerStatsChart.getDescription().setEnabled(false);
        playerStatsChart.invalidate(); // refresh
    }

    private void displayShootingAccuracyChart(List<PlayerStatsView> statsList) {
        List<BarEntry> entries = new ArrayList<>();
        List<String> playerNames = new ArrayList<>();
        int index = 0;

        for (PlayerStatsView stats : statsList) {
            int attempts = stats.getGoal() + stats.getPenalty_goal() + stats.getGoal_missed();
            float accuracy = attempts > 0 ? ((float)(stats.getGoal() + stats.getPenalty_goal()) / attempts) * 100 : 0f;
            entries.add(new BarEntry(index, accuracy));
            playerNames.add(stats.getPlayer_name());
            index++;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Shooting Accuracy (%)");
        dataSet.setColors(com.github.mikephil.charting.utils.ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f);

        BarData barData = new BarData(dataSet);
        shootingAccuracyChart.setData(barData);

        shootingAccuracyChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(playerNames));
        shootingAccuracyChart.getXAxis().setGranularity(1f);
        shootingAccuracyChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        shootingAccuracyChart.getAxisRight().setEnabled(false);
        shootingAccuracyChart.getDescription().setEnabled(false);

        shootingAccuracyChart.invalidate();
    }

    private void displayErrorsChart(List<PlayerStatsView> statsList) {
        List<BarEntry> entries = new ArrayList<>();
        List<String> playerNames = new ArrayList<>();
        int index = 0;

        for (PlayerStatsView stats : statsList) {
            entries.add(new BarEntry(index, new float[]{
                    stats.getDrop_ball(),
                    stats.getHeld_ball(),
                    stats.getStepping(),
                    stats.getContact(),
                    stats.getObstruction()
            }));
            playerNames.add(stats.getPlayer_name());
            index++;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Errors & Penalties");
        dataSet.setColors(new int[]{
                Color.RED, Color.MAGENTA, Color.YELLOW, Color.BLUE, Color.GREEN
        });
        dataSet.setStackLabels(new String[]{"Drop", "Held", "Step", "Contact", "Obstruction"});

        BarData barData = new BarData(dataSet);
        errorsChart.setData(barData);

        errorsChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(playerNames));
        errorsChart.getXAxis().setGranularity(1f);
        errorsChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        errorsChart.getAxisRight().setEnabled(false);
        errorsChart.getDescription().setEnabled(false);

        errorsChart.invalidate();
    }

    private void displayContributionsChart(PlayerStatsView stats) {
        List<RadarEntry> entries = new ArrayList<>();
        entries.add(new RadarEntry(stats.getIntercept()));
        entries.add(new RadarEntry(stats.getDeflection()));
        entries.add(new RadarEntry(stats.getGoal_assist()));
        entries.add(new RadarEntry(stats.getOffensive_rebound()));
        entries.add(new RadarEntry(stats.getDefensive_rebound()));

        RadarDataSet dataSet = new RadarDataSet(entries, stats.getPlayer_name());
        dataSet.setColor(Color.BLUE);
        dataSet.setFillColor(Color.CYAN);
        dataSet.setDrawFilled(true);

        RadarData data = new RadarData(dataSet);
        contributionsChart.setData(data);

        contributionsChart.getDescription().setEnabled(false);
        contributionsChart.invalidate();
    }

    private void displayTeamShootingChart(PlayerStatsView totals) {
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(totals.getGoal(), "Goals"));
        entries.add(new PieEntry(totals.getPenalty_goal(), "Penalty Goals"));
        entries.add(new PieEntry(totals.getGoal_missed(), "Missed"));

        PieDataSet dataSet = new PieDataSet(entries, "Team Shooting Breakdown");
        dataSet.setColors(com.github.mikephil.charting.utils.ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueTextSize(12f);

        PieData data = new PieData(dataSet);
        teamShootingChart.setData(data);
        teamShootingChart.getDescription().setEnabled(false);
        teamShootingChart.invalidate();
    }
}
