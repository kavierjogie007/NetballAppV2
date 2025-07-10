package com.example.netballapp;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Player {
    @SerializedName("player_ID")
    public Long player_ID;

    @SerializedName("player_firstname")
    public String player_FirstName;

    @SerializedName("player_surname")
    public String player_Surname;

    @SerializedName("player_number")
    public Integer player_Number;

    @SerializedName("player_position")
    public String player_position;

    @SerializedName("player_dob")
    public String player_DOB;

    @SerializedName("player_height")
    public Integer player_Height;

    public String getPlayer_FirstName() {
        return player_FirstName;
    }

    public String getPlayer_Surname() {
        return player_Surname;
    }

    public long getPlayer_ID() {
        return player_ID;
    }
}
