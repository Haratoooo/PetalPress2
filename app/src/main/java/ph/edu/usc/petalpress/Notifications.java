package ph.edu.usc.petalpress;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Notifications extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Edge-to-edge layout for fullscreen
        setContentView(R.layout.activity_notifications);

        // Set up the Home button (navigate to Homepage)
        ImageView navHome = findViewById(R.id.nav_home);
        navHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Homepage
                Intent homeIntent = new Intent(Notifications.this, Homepage.class);
                startActivity(homeIntent);
            }
        });

        // Set up the Settings button (navigate to Settings)
        ImageView navSettings = findViewById(R.id.nav_settings);
        navSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Settings
                Intent settingsIntent = new Intent(Notifications.this, Settings.class);
                startActivity(settingsIntent);
            }
        });

        // Set up the Back button (navigate to Settings)
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to Settings
                Intent backIntent = new Intent(Notifications.this, Settings.class);
                startActivity(backIntent);
            }
        });
    }
}
