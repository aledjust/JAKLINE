package com.aledgroup.jakline.utility;

/**
 * Created by razor on 4/8/15.
 */
public class Constants {

    public static final String MARKER_POSITION_KEY = "MarkerPosition";
    public static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 1000;
    public static final int DEFAULT_ZOOM = 15;

    public static long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    public static long FASTEST_INTERVAL = 2000; /* 2 sec */

    public static final String PREF_PICKUP_LATLNG = "PREFS_PICKUP_LATLNG";
    public static final String PREF_DROP_LATLNG = "PREFS_PICKUP_LATLNG";

    /**
     * Request code for location permission request.
     */
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    public static final String API_NOT_CONNECTED = "Google API not connected";
    public static final String SOMETHING_WENT_WRONG = "OOPs!!! Something went wrong...";
    public static String PlacesTag = "Google Places Auto Complete";


    // flag to identify whether to show single line
    // or multi line test push notification tray
    public static boolean appendNotificationMessages = true;

    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";
}
