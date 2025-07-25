package ph.edu.usc.petalpress;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Set up the Home button (navigate to Homepage)
        ImageView navHome = findViewById(R.id.nav_home);
        navHome.setOnClickListener(v -> {
            // Navigate to Homepage
            Intent homeIntent = new Intent(Settings.this, Homepage.class);
            startActivity(homeIntent);
        });



        // Set up the Settings Card click (navigate to Notifications)
        View settingsCard = findViewById(R.id.card_notif); // Assuming this is the card element's ID
        settingsCard.setOnClickListener(v -> {
            // Navigate to NotificationsActivity when the settings card is clicked
            Intent notificationsIntent = new Intent(Settings.this, Notifications.class);
            startActivity(notificationsIntent);
        });

        // Set up the Sign-Out button
        AppCompatButton signOutButton = findViewById(R.id.signOutButton);  // Use AppCompatButton
        signOutButton.setOnClickListener(v -> signOut());
    }

    // Method to sign out using SupabaseService
    private void signOut() {
        String userToken = getUserToken();  // Retrieve the stored user token (from SharedPreferences)

        // Show Toast indicating the sign-out is in progress
        Toast.makeText(Settings.this, "Signing out...", Toast.LENGTH_SHORT).show();

        // Perform sign-out asynchronously
        SupabaseService.signOutAsync(userToken);  // Calling the async task for sign-out

        // Show message to the user (in case the task completes in the background)
        Toast.makeText(Settings.this, "Signed out successfully", Toast.LENGTH_SHORT).show();

        // Clear the user token after sign-out (you may want to do this after confirming success)
        clearUserToken();

        // Redirect to the login screen
        Intent loginIntent = new Intent(Settings.this, LetsGetStartedActivity.class);  // Redirect to your login or signup activity
        startActivity(loginIntent);
        finish();  // Close current activity
    }

    // Helper method to get the user token (from SharedPreferences)
    private String getUserToken() {
        return getSharedPreferences("user_prefs", MODE_PRIVATE)
                .getString("user_token", null);  // Retrieve token from SharedPreferences
    }

    // Helper method to clear the user token from SharedPreferences
    private void clearUserToken() {
        getSharedPreferences("user_prefs", MODE_PRIVATE)
                .edit()
                .remove("user_token")  // Clear the user token
                .apply();
    }
}
