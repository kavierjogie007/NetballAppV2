package com.example.netballapp.Model;

public class Court
{
    private Long court_ID;
    private String court_position_field;
    private Long player_id;
    private Long game_id;

    public Long getCourt_ID() {
        return court_ID;
    }

    public void setCourt_ID(Long court_ID) {
        this.court_ID = court_ID;
    }

    public String getCourt_position_field() {
        return court_position_field;
    }

    public void setCourt_position_field(String court_position_field) {
        this.court_position_field = court_position_field;
    }

    public Long getPlayer_id() {
        return player_id;
    }

    public void setPlayer_id(Long player_id) {
        this.player_id = player_id;
    }

    public Long getGame_id() {
        return game_id;
    }

    public void setGame_id(Long game_id) {
        this.game_id = game_id;
    }

    public Court(String positionField, Long game_ID, Long player_ID) {
        this.court_position_field = positionField;
        this.player_id = player_ID;
        this.game_id = game_ID;
    }
}

