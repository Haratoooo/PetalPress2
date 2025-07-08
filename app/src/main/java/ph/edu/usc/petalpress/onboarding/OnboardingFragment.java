package ph.edu.usc.petalpress.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class OnboardingFragment extends Fragment {

    private static final String ARG_LAYOUT_ID = "layout_id";

    public static OnboardingFragment newInstance(int layoutId) {
        OnboardingFragment fragment = new OnboardingFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_ID, layoutId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int layoutId = getArguments().getInt(ARG_LAYOUT_ID);
        return inflater.inflate(layoutId, container, false);
    }
}
