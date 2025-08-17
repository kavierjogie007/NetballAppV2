package com.example.netballapp.Model;

public class Court
{
    private Long court_ID;
    private String court_position_field;
    private Long player_id;
    private Long game_id;

    public Court(String positionField, Long game_ID,Long player_ID) {
        this.court_position_field = positionField;
        this.player_id = player_ID;
        this.game_id = game_ID;
    }
}

