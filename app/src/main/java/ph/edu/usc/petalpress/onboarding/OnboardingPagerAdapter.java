package ph.edu.usc.petalpress.onboarding;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class OnboardingPagerAdapter extends FragmentStateAdapter {

    private final int[] layouts;

    public OnboardingPagerAdapter(@NonNull FragmentActivity fragmentActivity, int[] layouts) {
        super(fragmentActivity);
        this.layouts = layouts;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return OnboardingFragment.newInstance(layouts[position]);
    }

    @Override
    public int getItemCount() {
        return layouts.length;
    }
}
