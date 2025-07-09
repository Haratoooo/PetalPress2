package ph.edu.usc.petalpress;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class LetsGetStartedActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lets_get_started);

        findViewById(R.id.btnGoogle).setOnClickListener(v -> {
            // TODO: Integrate Google Sign-In
        });

        findViewById(R.id.btnApple).setOnClickListener(v -> {
            // TODO: Integrate Apple Sign-In
        });

        findViewById(R.id.btnSignUp).setOnClickListener(v ->
                startActivity(new Intent(this, SignUpActivity.class))
        );

        findViewById(R.id.btnSignIn).setOnClickListener(v ->
                startActivity(new Intent(this, SignInActivity.class))
        );
    }
}
