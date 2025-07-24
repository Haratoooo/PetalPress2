package ph.edu.usc.petalpress;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class JournalEntryActivity extends AppCompatActivity {

    private LinearLayout toolbarLayout, textParentWrapper;
    private ImageButton toggleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_entry);

        // Initialize views
        toolbarLayout = findViewById(R.id.toolbar_toggle);  // The toolbar layout to toggle
        textParentWrapper = findViewById(R.id.textparentwrapper);  // The content layout
        toggleButton = findViewById(R.id.toolbar_toggle_btn);  // Toggle button

        // Set the initial visibility of the toolbar to gone (hidden)
        toolbarLayout.setVisibility(View.GONE);

        // Toggle the toolbar visibility and adjust the content layout
        toggleButton.setOnClickListener(v -> {
            if (toolbarLayout.getVisibility() == View.VISIBLE) {
                // Hide the toolbar
                toolbarLayout.setVisibility(View.GONE);
                // Adjust content layout to be below the header when toolbar is hidden
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) textParentWrapper.getLayoutParams();
                params.addRule(RelativeLayout.BELOW, R.id.header);  // Set textParentWrapper below the header
                textParentWrapper.setLayoutParams(params);
            } else {
                // Show the toolbar
                toolbarLayout.setVisibility(View.VISIBLE);
                // Adjust content layout to be below the toolbar when it's visible
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) textParentWrapper.getLayoutParams();
                params.addRule(RelativeLayout.BELOW, R.id.toolbar_toggle);  // Set textParentWrapper below the toolbar
                textParentWrapper.setLayoutParams(params);
            }
        });
    }
}


