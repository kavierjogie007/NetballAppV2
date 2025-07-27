package com.example.netballapp.api;

import com.example.netballapp.Model.Coach;
import com.example.netballapp.Model.Court;
import com.example.netballapp.Model.Game;
import com.example.netballapp.Model.Player;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.Query;
import retrofit2.http.POST;

/**
 * SupabaseApi interface to define the API endpoints
 * This interface defines the API endpoints for the Supabase API
 * It uses Retrofit annotations to define the HTTP methods and parameters
 * The methods defined in this interface will be used to make API calls
 * Typically,  GET annotations are used to fetch data from the API
 * The PUT annotation is used to insert data into the database
 * I would advise you to put all your api calls here, so login, add user, etc.
 */

public interface SuperbaseAPI {
        //Coach table
        @GET("/rest/v1/coach") // Your table name is "Example"
        Call<List<Coach>> getItems(
                @Query("select") String select); //first part of your query (select, update, insert, etc.)

        @GET("rest/v1/coach")
        Call<List<Coach>> loginCoach(
                @Query("coach_username") String username,
                @Query("coach_password") String password);

        // GET coach by ID
        @GET("rest/v1/coach")
        Call<List<Coach>> getCoachById(
                @Query("coach_ID") String idFilter); // Use "eq.1"

        // PATCH update coach
        @PATCH("rest/v1/coach")
        @Headers({"Prefer: return=representation"})
        Call<List<Coach>> updateCoachProfile(
                @Query("coach_ID") String idFilter,
                @Body Coach updatedCoach);

        @POST("rest/v1/coach")
        @Headers({"Prefer: return=representation"})
        Call<List<Coach>> registerCoach(
                @Body Coach coach);

        // GET player by ID
        @GET("rest/v1/player")
        Call<List<Player>> getPlayerById(
                @Query("player_ID") String idFilter); // Use "eq.1"

        @GET("rest/v1/player")
        Call<List<Player>> getPlayers(
                @Query("select") String select // e.g., "*"
        );

        @POST("rest/v1/player")
        @Headers({"Prefer: return=representation"})
        Call<List<Player>> registerPlayer(
                @Body Player player);

        @PATCH("rest/v1/player")
        @Headers({"Prefer: return=representation"})
        Call<List<Player>> updatePlayerProfile(
                @Query("player_ID") String idFilter,
                @Body Player updatedPlayer);

        @DELETE("rest/v1/player")
        @Headers("Prefer: return=minimal")
        Call<Void> deletePlayer(
                @Query("player_ID") String idFilter);

        @POST("rest/v1/game")
        @Headers({"Prefer: return=representation"})
        Call<List<Game>> setUpNewGame(
                @Body Game game);

        @POST("rest/v1/court")
        @Headers({"Prefer: resolution=merge-duplicates", "Prefer: return=representation"})
        Call<List<Court>> assignPlayerToCourt(
                @Body Court assignment);

}

