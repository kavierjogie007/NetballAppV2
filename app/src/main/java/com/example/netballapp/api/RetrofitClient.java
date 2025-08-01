package com.example.netballapp.api;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;
    private static String baseUrl = "https://ndgsgfhlqddnwtaqxken.supabase.co";
    //API to authorize access to Supabase
    private static String apiKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im5kZ3NnZmhscWRkbnd0YXF4a2VuIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTEyMDY4ODMsImV4cCI6MjA2Njc4Mjg4M30.05iDZzTd7u8xCjXIYniCHt7STPzQavwJLd0G638H1Sc";

    // creates a Retrofit client if one is already created, it will return the existing one
    //Used to create http requests
    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl) //Sets base URL
                    .client(buildHttpClient()) //Adds a OkHttpClient
                    .addConverterFactory(GsonConverterFactory.create()) //Uses Gson to convert JSON to Java objects
                    .build();
        }
        return retrofit;
    }

    private static OkHttpClient buildHttpClient() {
        return new OkHttpClient.Builder()
                //Interceptor modifies every outgoing Http request to include headers
                .addInterceptor(chain -> {
                    Request request = chain.request().newBuilder()
                            //Required by Supabase to know which project is making the call
                            .addHeader("apikey", apiKey)
                            //Required by Supabase to know who the caller is and what they allowed to do
                            .addHeader("Authorization", "Bearer " + apiKey)
                            //tells server JSON data will be sent
                            .addHeader("Content-Type", "application/json")
                            .build();
                    return chain.proceed(request);
                })
                .build();
    }
}
