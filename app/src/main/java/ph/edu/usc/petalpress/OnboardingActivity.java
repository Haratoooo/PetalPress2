package ph.edu.usc.petalpress;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private OnboardingAdapter adapter;
    private Button getStartedButton;
    private ImageView dotIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        // Bind views
        viewPager = findViewById(R.id.viewPager);
        getStartedButton = findViewById(R.id.getStartedButton);
        dotIndicator = findViewById(R.id.dotIndicator);

        // Set up adapter
        adapter = new OnboardingAdapter(this);
        viewPager.setAdapter(adapter);

        // Show button only on last page
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                // Show/hide "Let's Get Started" button
                if (position == adapter.getItemCount() - 1) {
                    getStartedButton.setVisibility(View.VISIBLE);
                } else {
                    getStartedButton.setVisibility(View.INVISIBLE);
                }

                // Update dot indicator
                switch (position) {
                    case 0:
                        dotIndicator.setImageResource(R.drawable.dot_indicator_1);
                        break;
                    case 1:
                        dotIndicator.setImageResource(R.drawable.dot_indicator_2);
                        break;
                    // add more cases if you add more fragments
                }
            }
        });

        // Set up "Let's Get Started" button
        getStartedButton.setOnClickListener(v -> {
            startActivity(new Intent(OnboardingActivity.this, LetsGetStartedActivity.class));
            finish();
        });

        // Hide button initially
        getStartedButton.setVisibility(View.INVISIBLE);
    }
}
