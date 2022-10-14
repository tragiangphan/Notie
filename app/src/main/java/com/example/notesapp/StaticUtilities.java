package com.example.notesapp;

import android.content.Context;
import android.content.SharedPreferences;

public class StaticUtilities {
    public static void StoreLoggedUsername(Context context, String username) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("NotePref", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.apply();
    }

    public static String getUsername(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("NotePref", 0);
        return sharedPreferences.getString("username", null);
    }
}
