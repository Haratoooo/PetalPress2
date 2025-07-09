package ph.edu.usc.petalpress;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_TIME_OUT = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            // You can check Supabase session here. If logged in, go to HomePage.
            // Otherwise, go to OnboardingActivity.
            startActivity(new Intent(SplashActivity.this, OnboardingActivity.class));
            finish();
        }, SPLASH_TIME_OUT);
    }
}
