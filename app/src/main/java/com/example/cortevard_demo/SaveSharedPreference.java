package com.example.cortevard_demo;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import static com.example.cortevard_demo.PreferencesUtility.LOGGED_IN_PREF;
import static com.example.cortevard_demo.PreferencesUtility.NEW_VERSION;
import static com.example.cortevard_demo.PreferencesUtility.USER_EMAIL;
import static com.example.cortevard_demo.PreferencesUtility.USER_NAME;

public class SaveSharedPreference {
    static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Set the Login Status
     * @param context
     * @param loggedIn
     */
    public static void setLoggedIn(Context context, boolean loggedIn, String email, String username) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(LOGGED_IN_PREF, loggedIn);
        editor.putString(USER_EMAIL, email);
        editor.putString(USER_NAME, username);
        editor.apply();
    }

    public static void setNewVersion(Context context, boolean new_version) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(NEW_VERSION, new_version);
        editor.apply();
    }

    /**
     * Get the Login Status
     * @param context
     * @return boolean: login status
     */
    public static boolean getLoggedStatus(Context context) {
        return getPreferences(context).getBoolean(LOGGED_IN_PREF, false);
    }

    public static String getLoggedEmail(Context context) {
        return getPreferences(context).getString(USER_EMAIL,"");
    }

    public static String getLoggedUsername(Context context) {
        return getPreferences(context).getString(USER_NAME,"");
    }

    public static Boolean getNewVersion(Context context) {
        return getPreferences(context).getBoolean(NEW_VERSION,false);
    }
}
