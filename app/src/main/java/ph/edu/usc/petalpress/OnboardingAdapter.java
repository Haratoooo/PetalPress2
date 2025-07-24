package ph.edu.usc.petalpress;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class OnboardingAdapter extends FragmentStateAdapter {

    public OnboardingAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return OnboardingFragment.newInstance(
                        "Write\nDaily\nEntries!",
                        "Reflect on your thoughts and emotions, one entry at a time."
                );
            case 1:
                return OnboardingFragment.newInstance(
                        "Let’s\nwrite your\nJournal!",
                        "Connect with yourself and document your journey today."
                );
            default:
                return new Fragment(); // fallback
        }
    }

    @Override
    public int getItemCount() {
        return 2; // number of onboarding pages
    }
}
