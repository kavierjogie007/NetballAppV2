package com.example.netballapp.Model;

public class Court
{
    public Long court_ID;
    public String court_position_field;
    public Long player_id;
    public Long game_id;

    public Court(String positionField, Long game_ID,Long player_ID) {
        this.court_position_field = positionField;
        this.player_id = player_ID;
        this.game_id = game_ID;
    }

    // Getters and setters if needed
}

