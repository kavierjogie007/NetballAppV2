package com.example.netballapp.api;

import com.example.netballapp.Model.Coach;
import com.example.netballapp.Model.Court;
import com.example.netballapp.Model.Game;
import com.example.netballapp.Model.Player;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.Query;
import retrofit2.http.POST;

// Supabase Api interface defines API endpoints for the Supabase API
//Retrofit annotations are used to define the HTTP methods and parameters
//The methods defined in this interface are used to make API calls
public interface SuperbaseAPI {
    //	@GET	Read data
    //  @POST	Create (insert)
    //	@PATCH	Partial update (modify)
    //	@PUT	Full update (replace)

    //Coach table
    //Updates coach where coach_ID matches
    @PATCH("rest/v1/coach")
    @Headers({"Prefer: return=representation"}) //Returns the full updated row in the response body
    Call<List<Coach>> updateCoachProfile(
            @Query("coach_ID") String idFilter,
            @Body Coach updatedCoach);

    // GET coach by their ID
    @GET("rest/v1/coach")
    Call<List<Coach>> getCoachById(
            @Query("coach_ID") String idFilter);


    @GET("rest/v1/coach")//Calls the coach table
    Call<List<Coach>> loginCoach(
            @Query("coach_username") String username, //Adds coach_username   =eq.john to URL
            @Query("coach_password") String password); // Adds coach_password =eq.john to URL

    @POST("rest/v1/coach")
    @Headers({"Prefer: return=representation"})
    Call<List<Coach>> registerCoach(
            @Body Coach coach);

    //Player table
    @GET("rest/v1/player")
    Call<List<Player>> getPlayers(
            @Query("select") String select //first part of query (select, update, insert, etc.)
    );

    // GET player by ID
    @GET("rest/v1/player")
    Call<List<Player>> getPlayerById(
            @Query("player_ID") String idFilter);

    @POST("rest/v1/player")
    @Headers({"Prefer: return=representation"})
    Call<List<Player>> registerPlayer(
            @Body Player player); //Converts Player Java object into JSON and sends it as the request body.

    @PATCH("rest/v1/player")
    @Headers({"Prefer: return=representation"})
    Call<List<Player>> updatePlayerProfile(
            @Query("player_ID") String idFilter,
            @Body Player updatedPlayer);

    @DELETE("rest/v1/player")
    @Headers("Prefer: return=minimal") //Returns empty response
    Call<Void> deletePlayer(
            @Query("player_ID") String idFilter);

    //Game table
    @POST("rest/v1/game")
    @Headers({"Prefer: return=representation"})
    Call<List<Game>> setUpNewGame(
            @Body Game game);

    //Court table
    @POST("rest/v1/court")
    @Headers({"Prefer: resolution=merge-duplicates", "Prefer: return=representation"})
    Call<List<Court>> assignPlayerToCourt(
            @Body Court assignment);

    // Fetch all court assignments
    @GET("rest/v1/court")
    Call<List<Court>> getCourtAssignments(
            @Query("select") String select // e.g., "*,player(*)" to include related player data
    );

}

