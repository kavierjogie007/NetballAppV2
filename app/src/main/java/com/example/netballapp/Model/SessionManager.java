package com.example.netballapp.Model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.example.netballapp.activities.LoginActivity;

public class SessionManager {

    public static void logout(Context context) {
        // Clear shared prefs
        context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply();

        // Go back to login
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        if (context instanceof Activity) {
            ((Activity) context).finish();
        }
    }


}

