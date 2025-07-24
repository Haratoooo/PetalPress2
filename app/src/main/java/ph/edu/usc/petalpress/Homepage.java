package ph.edu.usc.petalpress;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Homepage extends AppCompatActivity {

    private RecyclerView recentlyOpenedRecyclerView;
    private RecentlyOpenedAdapter adapter;
    private List<Journal> journalList = new ArrayList<>();
    private List<Journal> filteredJournalList = new ArrayList<>(); // For recently opened
    private List<Journal> filteredCreatedJournals = new ArrayList<>(); // For journals you created

    private ViewFlipper journalFlipper;
    private ImageView arrowLeft, arrowRight;
    private TextView pageIndicator;
    private EditText searchBar; // Search bar reference

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        // Setup Recently Opened RecyclerView
        recentlyOpenedRecyclerView = findViewById(R.id.recentlyOpenedRecyclerView);
        recentlyOpenedRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );

        adapter = new RecentlyOpenedAdapter(this, filteredJournalList); // Use filtered list
        recentlyOpenedRecyclerView.setAdapter(adapter);

        // Setup ViewFlipper for 'Journals You Created'
        journalFlipper = findViewById(R.id.journalFlipper);
        arrowLeft = findViewById(R.id.arrowLeft);
        arrowRight = findViewById(R.id.arrowRight);
        pageIndicator = findViewById(R.id.pageIndicator);

        arrowLeft.setOnClickListener(v -> {
            journalFlipper.showPrevious();
            updatePageIndicator();
        });

        arrowRight.setOnClickListener(v -> {
            journalFlipper.showNext();
            updatePageIndicator();
        });

        // Setup Settings Button
        ImageView navSettings = findViewById(R.id.nav_settings);
        navSettings.setOnClickListener(v -> {
            // Navigate to SettingsActivity when settings icon is clicked
            Intent intent = new Intent(Homepage.this, Settings.class);
            startActivity(intent);
        });

        // Setup the search bar functionality
        searchBar = findViewById(R.id.searchBar); // Now this refers to the correct ID in your layout
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                filterJournals(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Load journals in background thread
        new Thread(() -> {
            List<Journal> fetched = loadJournalsFromSupabase();

            runOnUiThread(() -> {
                journalList.clear();
                journalList.addAll(fetched);

                // Initially show all journals in both lists
                filteredJournalList.clear();
                filteredJournalList.addAll(fetched); // Recently Opened list

                filteredCreatedJournals.clear();
                filteredCreatedJournals.addAll(fetched); // Journals You Created list

                adapter.notifyDataSetChanged();

                populateJournalFlipper(); // 🔧 Call dynamic flipper population
            });
        }).start();
    }

    private void updatePageIndicator() {
        int current = journalFlipper.getDisplayedChild() + 1;
        int total = journalFlipper.getChildCount();
        pageIndicator.setText(current + " / " + total);
    }

    private List<Journal> loadJournalsFromSupabase() {
        List<Journal> journals = new ArrayList<>();
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String token = prefs.getString("access_token", null);

            if (token == null) {
                Log.e("Homepage", "Access token missing");
                return journals;
            }

            String json = SupabaseService.fetchJournals(token);
            if (json == null) return journals;

            Log.d("SupabaseJournals", "Raw JSON response: " + json);  // 🔍 log the full JSON response

            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                Journal journal = Journal.fromJson(this, obj);

                Log.d("SupabaseJournals", "fromJson → Title: " + journal.getTitle() +
                        ", Description: " + journal.getDescription() +
                        ", ImageResId: " + journal.getImageResId());

                journals.add(journal);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return journals;
    }

    // 🔄 Dynamically populate ViewFlipper from journalList
    private void populateJournalFlipper() {
        journalFlipper.removeAllViews();
        int pageSize = 3;
        LayoutInflater inflater = LayoutInflater.from(this);

        // Populate for 'Journals You Created' (filteredCreatedJournals)
        for (int i = 0; i < filteredCreatedJournals.size(); i += pageSize) {
            LinearLayout pageLayout = new LinearLayout(this);
            pageLayout.setOrientation(LinearLayout.VERTICAL);
            pageLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            int end = Math.min(i + pageSize, filteredCreatedJournals.size());
            List<Journal> pageJournals = filteredCreatedJournals.subList(i, end);

            for (Journal journalObj : pageJournals) {
                final Journal journal = journalObj;  // 🟣 prevent loop mutation bug

                View card = inflater.inflate(R.layout.journal_card, pageLayout, false);

                TextView title = card.findViewById(R.id.journalTitle);
                TextView description = card.findViewById(R.id.journalDescription);
                ImageView image = card.findViewById(R.id.journalImage);

                title.setText(journal.getTitle());
                description.setText("Created on " + journal.getCreatedAt());
                image.setImageResource(journal.getImageResId());

                // 🟣 Intent navigation with proper extras
                card.setOnClickListener(v -> {
                    Log.d("Homepage", "Passing intent - title: " + journal.getTitle()
                            + ", desc: " + journal.getDescription()
                            + ", imageResId: " + journal.getImageResId());

                    Intent intent = new Intent(Homepage.this, EntriesList.class);
                    intent.putExtra("journal_id", journal.getId());
                    intent.putExtra("journal_title", journal.getTitle());
                    intent.putExtra("journal_image", journal.getImageResId());
                    intent.putExtra("journal_description", journal.getDescription());

                    Log.d("IntentDebug", "Sending title: " + journal.getTitle()
                            + ", desc: " + journal.getDescription()
                            + ", imageResId: " + journal.getImageResId());

                    startActivity(intent);
                });

                pageLayout.addView(card);
            }

            journalFlipper.addView(pageLayout);
        }

        updatePageIndicator();
    }

    // Filter journals by title
    private void filterJournals(String query) {
        // Filter Recently Opened list
        List<Journal> filteredList = new ArrayList<>();
        for (Journal journal : journalList) {
            if (journal.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(journal);
            }
        }

        filteredJournalList.clear();
        filteredJournalList.addAll(filteredList); // Update the filtered list for recently opened journals
        adapter.notifyDataSetChanged(); // Notify the adapter to update the RecyclerView

        // Filter Journals You Created list
        List<Journal> filteredCreatedList = new ArrayList<>();
        for (Journal journal : journalList) {
            if (journal.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredCreatedList.add(journal);
            }
        }

        filteredCreatedJournals.clear();
        filteredCreatedJournals.addAll(filteredCreatedList); // Update the filtered list for created journals
        populateJournalFlipper(); // Update the ViewFlipper for 'Journals You Created'
    }
}
