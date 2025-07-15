package com.example.netballapp;

import com.google.gson.annotations.SerializedName;

public class Game {
    @SerializedName("game_ID")
    public Long game_ID;

    @SerializedName("game_name")
    public String game_Name;

    @SerializedName("game_opposition_name")
    public String game_OppositionName;

    @SerializedName("game_venue")
    public String game_Venue;

    @SerializedName("game_date")
    public String game_Date;

    @SerializedName("game_type")
    public String game_Type;

    @SerializedName("game_bench_positions")
    public Integer game_BenchPositions;

    @SerializedName("game_madibaz_score")
    public Integer game_MadibazScore;

    @SerializedName("game_opposition_score")
    public Integer game_OppositionScore;

    @SerializedName("game_timer")
    public Integer game_Timer;

    @SerializedName("game_current_centre_pass_team")
    public String game_CurrentCentrePassTeam;

    @SerializedName("game_coach_note")
    public String game_CoachNote;
}
