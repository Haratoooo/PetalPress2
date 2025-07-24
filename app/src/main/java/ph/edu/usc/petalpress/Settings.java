package ph.edu.usc.petalpress;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Edge-to-edge layout for fullscreen
        setContentView(R.layout.activity_settings);

        // Set up the Home button (navigate to Homepage)
        ImageView navHome = findViewById(R.id.nav_home);
        navHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Homepage
                Intent homeIntent = new Intent(Settings.this, Homepage.class);
                startActivity(homeIntent);
            }
        });

        // Set up the Notification Bell button (navigate to Notifications)
        ImageView notificationBell = findViewById(R.id.notificationBell);
        notificationBell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to NotificationsActivity
                Intent notificationsIntent = new Intent(Settings.this, Notifications.class);
                startActivity(notificationsIntent);
            }
        });
    }
}
