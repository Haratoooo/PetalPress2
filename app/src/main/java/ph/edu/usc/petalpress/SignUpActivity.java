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

import ph.edu.usc.petalpress.R;

public class SignUpActivity extends AppCompatActivity {

    EditText emailEditText, passwordEditText, confirmPasswordEditText;
    Button signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        signupButton = findViewById(R.id.signupButton);
        TextView signinText = findViewById(R.id.signinText);

        SpannableString spanText = new SpannableString("Already have an account? Sign-in");

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        };

        spanText.setSpan(clickableSpan, 25, 32, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // "Sign-in" position

        signinText.setText(spanText);
        signinText.setMovementMethod(LinkMovementMethod.getInstance());


        signupButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String pass = passwordEditText.getText().toString();
            String confirm = confirmPasswordEditText.getText().toString();

            if (!pass.equals(confirm)) {
                Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                int responseCode = SupabaseService.signUp(email, pass);

                runOnUiThread(() -> {
                    if (responseCode == 200 || responseCode == 201) {
                        Toast.makeText(this, "Sign-up successful!", Toast.LENGTH_SHORT).show();
                        // Redirect to WelcomeActivity (optional)
                        Intent intent = new Intent(this, WelcomeActivity.class);
                        intent.putExtra("USER_EMAIL", email);
                        startActivity(intent);
                    } else if (responseCode == 400) {
                        Toast.makeText(this, "Email already registered.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Error: " + responseCode, Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        });


    }
}
