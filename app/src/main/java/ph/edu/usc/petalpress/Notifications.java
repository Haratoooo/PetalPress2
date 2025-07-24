package ph.edu.usc.petalpress;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

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

        // 1. Build your static list
        List<NotificationItem> notifications = new ArrayList<>();

// first notification
        notifications.add(new NotificationItem(
                "Journal Entry",
                "Don't forget to log your thoughts today in 'Morning Whispers.'",
                "5 minutes ago"
        ));

// second notification
        notifications.add(new NotificationItem(
                "New Message",
                "Alex sent you a message about today’s meeting.",
                "10 minutes ago"
        ));

// third notification
        notifications.add(new NotificationItem(
                "Daily Tip",
                "Try writing for 5 minutes using only emojis!",
                "1 hour ago"
        ));

// …and so on
        // add more items as needed…

        // 2. Find and configure RecyclerView
        RecyclerView recyclerView = findViewById(R.id.notificationsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new NotificationAdapter(notifications));
    }
}
