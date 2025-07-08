package com.example.netballapp;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * SupabaseClient class to create a Retrofit client
 * Essentially, this class is a singleton class that creates a Retrofit client
 * If one is already created, it will return the existing one
 */

public class RetrofitClient {
    private static Retrofit retrofit = null;
    private static String baseUrl = "https://ndgsgfhlqddnwtaqxken.supabase.co";
    private static String apiKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im5kZ3NnZmhscWRkbnd0YXF4a2VuIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTEyMDY4ODMsImV4cCI6MjA2Njc4Mjg4M30.05iDZzTd7u8xCjXIYniCHt7STPzQavwJLd0G638H1Sc";
    public static Retrofit getClient() {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(chain -> {
                Request request = chain.request().newBuilder()
                        .addHeader("apikey", apiKey)
                        .addHeader("Authorization", "Bearer " + apiKey)
                        .addHeader("Content-Type", "application/json")
                        .build();
                return chain.proceed(request);
            }).build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
