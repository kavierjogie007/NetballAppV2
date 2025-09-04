package com.example.netballapp.Model;

public class CoachGame {
    private Long coach_ID;
    private Long game_ID;

    public CoachGame(Long coach_ID, Long game_ID) {
        this.coach_ID = coach_ID;
        this.game_ID = game_ID;
    }

    public Long getCoach_ID() { return coach_ID; }
    public void setCoach_ID(Long coach_ID) { this.coach_ID = coach_ID; }

    public Long getGame_ID() { return game_ID; }
    public void setGame_ID(Long game_ID) { this.game_ID = game_ID; }
}
