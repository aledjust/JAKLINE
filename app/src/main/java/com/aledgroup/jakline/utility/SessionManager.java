package com.aledgroup.jakline.utility;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.aledgroup.jakline.model.User;

/**
 * Created by aled on 04/13/2016.
 */
public class SessionManager {

    // Shared Preferences
    SharedPreferences pref;
    // Editor for Shared preferences
    Editor editor;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "JAKLINEPref";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";

    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";

    public static final String KEY_PICKUP_LAT = "pickup_Lat";
    public static final String KEY_PICKUP_LONG = "pickup_Long";
    public static final String KEY_DROPOFF_LAT = "dropoff_Lat";
    public static final String KEY_DROPOFF_LONG = "dropoff_Long";

    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_NOTIFICATIONS = "notifications";

    public SessionManager(){
        super();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(Context _context, String name, String email){

        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();

        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        // Storing name in pref
        editor.putString(KEY_NAME, name);
        // Storing email in pref
        editor.putString(KEY_EMAIL, email);

        // commit changes
        editor.commit();
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(Context _context){
        // Check login status
        if(!this.isLoggedIn(_context)){
            // user is not logged in redirect him to Login Activity
            /*Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);*/
        }

    }


    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));

        // user email id
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));

        // return user
        return user;
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        /*editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);*/
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(Context _context){
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        return pref.getBoolean(IS_LOGIN, false);
    }

    public void SET_PICKUP_LAT(Context _context, String pickupLat)
    {
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        editor.putString(KEY_PICKUP_LAT, pickupLat);
        editor.commit();
    }
    public void SET_PICKUP_LONG(Context _context, String pickupLong)
    {
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        editor.putString(KEY_PICKUP_LONG, pickupLong);
        editor.commit();
    }
    public void SET_DROPOFF_LAT(Context _context, String dropoffLat)
    {
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        editor.putString(KEY_DROPOFF_LAT, dropoffLat);
        editor.commit();
    }
    public void SET_DROPOFF_LONG(Context _context, String dropoffLong)
    {
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        editor.putString(KEY_DROPOFF_LONG, dropoffLong);
        editor.commit();
    }

    public String getKeyPickupLat(Context _context) {
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        return pref.getString(KEY_PICKUP_LAT, "0");
    }
    public String getKeyPickupLong(Context _context) {
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        return pref.getString(KEY_PICKUP_LONG, "0");
    }
    public String getKeyDropoffLat(Context _context) {
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        return pref.getString(KEY_DROPOFF_LAT, "0");
    }
    public String getKeyDropoffLong(Context _context) {
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        return pref.getString(KEY_DROPOFF_LONG, "0");
    }

    public void ClearSession(Context _context)
    {
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        editor.clear();
        editor.commit();
    }

    public User getUser() {
        if (pref.getString(KEY_USER_ID, null) != null) {
            String id, name, email;
            id = pref.getString(KEY_USER_ID, null);
            name = pref.getString(KEY_USER_NAME, null);
            email = pref.getString(KEY_USER_EMAIL, null);

            User user = new User(id, name, email);
            return user;
        }
        return null;
    }
}
