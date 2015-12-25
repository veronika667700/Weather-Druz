package com.druzsan.druzweatherv06;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    private static long SPLASH_SCREEN_DELAY_MILLIS = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.SplashTheme);
        setContentView(R.layout.activity_splash);

        Typeface typefaceThin = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");
        Typeface typefaceBold = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf");
        TextView tvSplash1 = (TextView) findViewById(R.id.tv_splash_1);
        TextView tvSplash2 = (TextView) findViewById(R.id.tv_splash_2);
        tvSplash1.setTypeface(typefaceBold);
        tvSplash2.setTypeface(typefaceThin);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(SplashActivity.this,
                        MainActivity.class);
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();
            }
        }, SPLASH_SCREEN_DELAY_MILLIS);
    }

}
