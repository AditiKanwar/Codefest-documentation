package com.example.dell.swachhpu;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class Splash_screen extends AppCompatActivity {

    private static int Splash_to = 3000;
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        getSupportActionBar().hide();

        statusbarcolor();

        img = (ImageView)findViewById(R.id.imageView);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade);
        img.startAnimation(animation);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                        .getBoolean("isFirstRun", true);

                if (isFirstRun) {
                    //show start activity
                    startActivity(new Intent(Splash_screen.this, Ob1.class));
                    finish();
                } else {

                    Intent intent = new Intent(Splash_screen.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }


                getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                        .putBoolean("isFirstRun", false).commit();

            }
        },Splash_to);

    }
     public void statusbarcolor() {

            android.view.Window window = this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
            window.setStatusBarColor(android.support.v4.content.ContextCompat.getColor(this,R.color.offGrey));

    }
}


