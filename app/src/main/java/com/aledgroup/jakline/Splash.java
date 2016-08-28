package com.aledgroup.jakline;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

/**
 * Created by aled on 03/25/2016.
 */
public class Splash extends Activity {

    //final Animation bounce = AnimationUtils.loadAnimation(this, R.anim.bounce);

    // Splash screen timer
    MediaPlayer mp;
    private static int SPLASH_TIME_OUT = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

     /*   ImageView imgLogo = (ImageView)findViewById(R.id.imgLogo);
        imgLogo.setAnimation(bounce);
*/
        mp = MediaPlayer.create(getBaseContext(), R.raw.jakline);
        mp.start();
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity

                Intent i = new Intent(Splash.this, MainActivity.class);
                finish();
                startActivity(i);

            }

        }, SPLASH_TIME_OUT);

    }
}
