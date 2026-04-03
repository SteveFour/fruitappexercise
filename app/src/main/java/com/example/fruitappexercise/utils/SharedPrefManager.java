package com.example.fruitappexercise.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    private static SharedPrefManager instance;
    private SharedPreferences sharedPreferences;

    private SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefManager(context);
        }
        return instance;
    }

    public void saveUserLogin(int userId, String username, String role) {
        sharedPreferences.edit()
                .putBoolean(Constants.PREF_IS_LOGGED_IN, true)
                .putInt(Constants.PREF_USER_ID, userId)
                .putString(Constants.PREF_USER_NAME, username)
                .putString(Constants.PREF_USER_ROLE, role)
                .apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(Constants.PREF_IS_LOGGED_IN, false);
    }

    public int getCurrentUserId() {
        return sharedPreferences.getInt(Constants.PREF_USER_ID, -1);
    }

    public String getUsername() {
        return sharedPreferences.getString(Constants.PREF_USER_NAME, "User");
    }

    public void logout() {
        sharedPreferences.edit().clear().apply();
    }
}