package com.example.netballapp.Model;

public class Court {
    private String court_PositionField;
    private Long player_ID;
    private Long game_ID;

    public Court(String positionField, Long player_ID, Long game_ID) {
        this.court_PositionField = positionField;
        this.player_ID = player_ID;
        this.game_ID = game_ID;
    }

    // Getters and setters if needed
}

