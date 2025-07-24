package ph.edu.usc.petalpress;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Homepage extends AppCompatActivity {

    private RecyclerView recentlyOpenedRecyclerView;
    private RecentlyOpenedAdapter adapter;
    private List<Journal> journalList = new ArrayList<>();
    private List<Journal> filteredJournalList = new ArrayList<>();
    private List<Journal> filteredCreatedJournals = new ArrayList<>();

    private ViewFlipper journalFlipper;
    private ImageView arrowLeft, arrowRight;
    private TextView pageIndicator;
    private EditText searchBar;

    private JournalRepository journalRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        journalRepo = new JournalRepository(this);

        recentlyOpenedRecyclerView = findViewById(R.id.recentlyOpenedRecyclerView);
        recentlyOpenedRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );
        adapter = new RecentlyOpenedAdapter(this, filteredJournalList);
        recentlyOpenedRecyclerView.setAdapter(adapter);

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

        // Set up the Home button (navigate to Homepage)
        ImageView navAddJournal = findViewById(R.id.nav_add_journal);
        navAddJournal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Homepage
                Intent homeIntent = new Intent(Homepage.this, AddJournalActivity.class);
                startActivity(homeIntent);
            }
        });

        // Set up the Settings button (navigate to Settings)
        ImageView navSettings = findViewById(R.id.nav_settings);
        navSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Settings
                Intent settingsIntent = new Intent(Homepage.this, Settings.class);
                startActivity(settingsIntent);
            }
        });

        searchBar = findViewById(R.id.searchBar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterJournals(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        new Thread(() -> {
            List<Journal> fetched = journalRepo.getAllJournals();
            runOnUiThread(() -> {
                journalList.clear();
                journalList.addAll(fetched);

                filteredJournalList.clear();
                filteredJournalList.addAll(fetched);

                filteredCreatedJournals.clear();
                filteredCreatedJournals.addAll(fetched);

                adapter.notifyDataSetChanged();
                populateJournalFlipper();
            });
        }).start();
    }

    private void updatePageIndicator() {
        int current = journalFlipper.getDisplayedChild() + 1;
        int total = journalFlipper.getChildCount();
        pageIndicator.setText(current + " / " + total);
    }

    private void populateJournalFlipper() {
        journalFlipper.removeAllViews();
        int pageSize = 3;
        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 0; i < filteredCreatedJournals.size(); i += pageSize) {
            LinearLayout pageLayout = new LinearLayout(this);
            pageLayout.setOrientation(LinearLayout.VERTICAL);

            int end = Math.min(i + pageSize, filteredCreatedJournals.size());
            List<Journal> pageJournals = filteredCreatedJournals.subList(i, end);

            for (Journal journal : pageJournals) {
                View card = inflater.inflate(R.layout.journal_card, pageLayout, false);

                TextView title = card.findViewById(R.id.journalTitle);
                TextView description = card.findViewById(R.id.journalDescription);
                ImageView image = card.findViewById(R.id.journalImage);

                title.setText(journal.title);
                description.setText("Created on " + journal.createdAt);

                if (journal.imagePath != null && !journal.imagePath.isEmpty()) {
                    image.setImageBitmap(BitmapFactory.decodeFile(journal.imagePath));
                } else {
                    image.setImageResource(R.drawable.petal_background); // fallback image
                }

                card.setOnClickListener(v -> {
                    Intent intent = new Intent(this, EntriesList.class);
                    intent.putExtra("journal_id", journal.id);
                    intent.putExtra("journal_title", journal.title);
                    intent.putExtra("journal_description", journal.description);
                    intent.putExtra("journal_image_path", journal.imagePath);
                    startActivity(intent);
                });

                pageLayout.addView(card);
            }

            journalFlipper.addView(pageLayout);
        }

        updatePageIndicator();
    }

    private void filterJournals(String query) {
        List<Journal> filteredList = new ArrayList<>();
        for (Journal journal : journalList) {
            if (journal.title.toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(journal);
            }
        }

        filteredJournalList.clear();
        filteredJournalList.addAll(filteredList);
        adapter.notifyDataSetChanged();

        filteredCreatedJournals.clear();
        filteredCreatedJournals.addAll(filteredList);
        populateJournalFlipper();
    }
}
