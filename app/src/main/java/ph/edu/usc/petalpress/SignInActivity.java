package ph.edu.usc.petalpress;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

public class SignInActivity extends AppCompatActivity {

    EditText emailEditText, passwordEditText;
    Button signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        signupButton = findViewById(R.id.signupButton);
        TextView signupText = findViewById(R.id.signupText);

        // "Sign-up" clickable span
        String fullText = "Don't have an account? Sign-up";
        SpannableString spannable = new SpannableString(fullText);
        int start = fullText.indexOf("Sign-up");
        int end = start + "Sign-up".length();

        spannable.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
            }
        }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        signupText.setText(spannable);
        signupText.setMovementMethod(LinkMovementMethod.getInstance());

        signupButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                // Get token from Supabase
                String tokenJson = SupabaseService.signInAndGetToken(SignInActivity.this, email, password);

                runOnUiThread(() -> {
                    if (tokenJson != null) {
                        try {
                            JSONObject obj = new JSONObject(tokenJson);
                            String accessToken = obj.getString("access_token");

                            // Save token to SharedPreferences
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                            prefs.edit().putString("access_token", accessToken).apply();

                            // Go to homepage
                            Intent intent = new Intent(this, WelcomeActivity.class);
                            intent.putExtra("USER_EMAIL", email);
                            startActivity(intent);
                            finish();

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Error parsing token", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Login failed! Check credentials.", Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        });
    }
}
