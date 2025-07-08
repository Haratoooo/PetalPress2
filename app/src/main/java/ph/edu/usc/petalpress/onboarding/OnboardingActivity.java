package ph.edu.usc.petalpress.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import ph.edu.usc.petalpress.R;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private LinearLayout indicatorLayout;
    private Button getStartedButton;

    private final int[] layouts = {
            R.layout.fragment_onboarding1,
            R.layout.fragment_onboarding2
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        viewPager = findViewById(R.id.viewPager);
        indicatorLayout = findViewById(R.id.indicators);
        getStartedButton = findViewById(R.id.getStartedButton);

        viewPager.setAdapter(new OnboardingPagerAdapter(this, layouts));
        setupIndicators(layouts.length);
        setCurrentIndicator(0);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            public void onPageSelected(int position) {
                setCurrentIndicator(position);
                getStartedButton.setVisibility(position == layouts.length - 1 ? View.VISIBLE : Button.INVISIBLE);
            }
        });

        getStartedButton.setOnClickListener(v -> {
            Intent intent = new Intent(OnboardingActivity.this, LetsGetStartedActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void setupIndicators(int count) {
        ImageView[] indicators = new ImageView[count];
        for (int i = 0; i < count; i++) {
            indicators[i] = new ImageView(this);
            indicators[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.indicator_inactive));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);
            indicatorLayout.addView(indicators[i], params);
        }
    }

    private void setCurrentIndicator(int index) {
        for (int i = 0; i < indicatorLayout.getChildCount(); i++) {
            ImageView imageView = (ImageView) indicatorLayout.getChildAt(i);
            imageView.setImageDrawable(ContextCompat.getDrawable(this,
                    i == index ? R.drawable.indicator_active : R.drawable.indicator_inactive));
        }
    }
}
