package ph.edu.usc.petalpress;

import android.content.Intent;
import android.os.Bundle;
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

public class SignInActivity extends AppCompatActivity {

    EditText emailEditText, passwordEditText;
    Button signupButton; // it's your "Sign in" button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin); // or your XML file

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        signupButton = findViewById(R.id.signupButton);
        // ID is still "signupButton"
        TextView signupText = findViewById(R.id.signupText);

        String fullText = "Don't have an account? Sign-up";
        SpannableString spannable = new SpannableString(fullText);

        int start = fullText.indexOf("Sign-up");
        int end = start + "Sign-up".length();

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        };

        spannable.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

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
                int code = SupabaseService.signIn(email, password);

                runOnUiThread(() -> {
                    if (code == 200) {
                        Intent intent = new Intent(this, WelcomeActivity.class);
                        intent.putExtra("USER_EMAIL", email);
                        startActivity(intent);
                        finish();
                    } else if (code == 400) {
                        Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Login failed! Code: " + code, Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        });
    }
}
