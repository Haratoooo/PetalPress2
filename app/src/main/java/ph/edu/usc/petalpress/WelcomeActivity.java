package ph.edu.usc.petalpress;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    TextView welcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        welcomeText = findViewById(R.id.welcomeText);

        // Get email from intent extras
        String userEmail = getIntent().getStringExtra("USER_EMAIL");
        welcomeText.setText("Welcome to PetalPress, " + userEmail + "!");
    }
}
