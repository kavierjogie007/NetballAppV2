package com.example.netballapp.Model;

import com.google.gson.annotations.SerializedName;

public class Player {
    @SerializedName("player_ID")
    public Long player_ID;

    @SerializedName("player_firstname")
    public String player_FirstName;

    public Player(String player_FirstName, String player_Surname) {
        this.player_FirstName = player_FirstName;
        this.player_Surname = player_Surname;
    }

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

    public Integer getPlayer_Number() {
        return player_Number;
    }

    public String getPlayer_position() {
        return player_position;
    }

    public String getPlayer_DOB() {
        return player_DOB;
    }

    public Player() {
    }

    public Integer getPlayer_Height() {
        return player_Height;
    }
}
