package com.aledgroup.jakline;

import android.app.Application;
import android.text.TextUtils;

import com.aledgroup.jakline.utility.SessionManager;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by aled on 04/16/2016.
 */
public class JaklineApp extends Application {
    public static final String TAG = JaklineApp.class
            .getSimpleName();

    private RequestQueue mRequestQueue;

    private static JaklineApp mInstance;

    private SessionManager session;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized JaklineApp getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public SessionManager getPrefManager() {
        if (session == null) {
            session = new SessionManager();
        }

        return session;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}
