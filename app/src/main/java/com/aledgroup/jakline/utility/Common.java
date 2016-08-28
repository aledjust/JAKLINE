package com.aledgroup.jakline.utility;

import android.content.Context;
import android.graphics.Typeface;
import android.location.Location;
import android.view.View;
import android.widget.TextView;

/**
 * Created by aled on 04/05/2016.
 */
public class Common {

    public static void SetCustomFont(Context context,View header, int id , String fontsName)
    {
        TextView tx = (TextView)header.findViewById(id);
        Typeface custom_font = Typeface.createFromAsset(context.getAssets(), "fonts/" +fontsName);
        tx.setTypeface(custom_font);
    }

    public static float getDistance(double startLati, double startLongi, double goalLati, double goalLongi){
        float[] resultArray = new float[99];
        Location.distanceBetween(startLati, startLongi, goalLati, goalLongi, resultArray);
        return resultArray[0];
    }
}
