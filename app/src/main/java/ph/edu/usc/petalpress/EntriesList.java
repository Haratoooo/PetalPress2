package ph.edu.usc.petalpress;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class EntriesList extends AppCompatActivity {

    private static final String TAG = "EntriesList";
    private RecyclerView entryRecyclerView;
    private EntryAdapter entryAdapter;
    private List<Entry> entries;
    private List<Entry> filteredEntries;  // List to hold filtered entries
    private EditText searchBar;  // Search bar reference

    private String journalId;  // Add this to hold the current journal ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entries_list);

        TextView journalTitleText = findViewById(R.id.journalTitle);
        TextView journalDescText = findViewById(R.id.journalDescription);
        ImageView journalImageView = findViewById(R.id.journalImage);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(EntriesList.this, Homepage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish(); // ✅ Optional: prevents returning to EntriesList
        });

        // 🖼 Log received Intent values
        String journalTitleStr = getIntent().getStringExtra("journal_title");
        String description = getIntent().getStringExtra("journal_description");
        int imageResId = getIntent().getIntExtra("journal_image", -1);
        journalId = getIntent().getStringExtra("journal_id");  // Get the journal ID

        Log.d("IntentDebug", "Received title: " + journalTitleStr
                + ", desc: " + description
                + ", imageResId: " + imageResId);

        // 🖼 Set header content
        journalTitleText.setText(journalTitleStr != null ? journalTitleStr : "(No Title)");
        journalDescText.setText(description != null ? description : "(No Description)");
        if (imageResId != -1) journalImageView.setImageResource(imageResId);

        // 🧱 RecyclerView setup
        entryRecyclerView = findViewById(R.id.entryRecyclerView);
        entryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        entries = new ArrayList<>();
        filteredEntries = new ArrayList<>();  // Initialize filteredEntries
        entryAdapter = new EntryAdapter(filteredEntries);  // Pass filtered entries to the adapter
        entryRecyclerView.setAdapter(entryAdapter);

        // Setup search bar functionality
        searchBar = findViewById(R.id.searchBar); // The ID of your search bar EditText
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                filterEntries(charSequence.toString());  // Filter entries as the text changes
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // ❌ Validate journal_id
        if (journalId == null || journalId.isEmpty()) {
            Log.e(TAG, "Missing journal_id");
            return;
        }

        // 🔑 Retrieve access token
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String token = prefs.getString("access_token", null);
        if (token == null) {
            Log.e(TAG, "Missing access token");
            return;
        }

        // 📡 Fetch entries in background
        new Thread(() -> {
            Log.d(TAG, "Fetching entries for journal_id: " + journalId);
            String response = SupabaseService.fetchEntries(token, journalId);

            if (response == null) {
                Log.e(TAG, "Entries response is null");
                return;
            }

            Log.d(TAG, "Entries response: " + response);

            try {
                JSONArray array = new JSONArray(response);
                List<Entry> fetchedEntries = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    String title = obj.getString("title");
                    String content = obj.getString("content");
                    String moodTag = obj.optString("mood_tag", "");
                    String mood1 = obj.getString("mood_1");
                    String mood2 = obj.getString("mood_2");
                    String createdAt = obj.getString("created_at");

                    int mood1ResId = getResources().getIdentifier(mood1, "drawable", getPackageName());
                    int mood2ResId = getResources().getIdentifier(mood2, "drawable", getPackageName());

                    String dateLabel = formatDate(createdAt);
                    String timeAgo = getTimeAgo(createdAt);

                    fetchedEntries.add(new Entry(title, content, dateLabel, timeAgo, mood1ResId, mood2ResId, moodTag));
                }

                runOnUiThread(() -> {
                    entries.clear();
                    entries.addAll(fetchedEntries);
                    filterEntries("");  // Initially show all entries when data is fetched
                    entryAdapter.notifyDataSetChanged();
                    Log.d(TAG, "Fetched " + fetchedEntries.size() + " entries.");
                });

            } catch (Exception e) {
                Log.e(TAG, "Error parsing entries", e);
            }
        }).start();

        // Handle the delete button click
        ImageButton deleteJournalButton = findViewById(R.id.deleteJournalButton);
        deleteJournalButton.setOnClickListener(v -> showDeleteConfirmationDialog());
    }

    // Show the delete journal confirmation dialog
    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Journal")
                .setMessage("Are you sure you want to delete this Journal?")
                .setCancelable(false)
                .setPositiveButton("Confirm", (dialog, which) -> {
                    // Handle journal deletion
                    deleteJournal();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Delete the journal (connect with Supabase to delete from the database)
    private void deleteJournal() {
        // Your code to delete the journal from Supabase
        Log.d(TAG, "Journal " + journalId + " deleted.");
        Toast.makeText(this, "Journal deleted", Toast.LENGTH_SHORT).show();
        finish(); // Optionally finish the activity after deletion
    }

    // Format the date to display
    private String formatDate(String isoDate) {
        try {
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            Date date = parser.parse(isoDate);
            SimpleDateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
            return formatter.format(date);
        } catch (ParseException e) {
            return "";
        }
    }

    // Get the time ago (e.g., "5 minutes ago")
    private String getTimeAgo(String isoDate) {
        try {
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            Date date = parser.parse(isoDate);
            long diff = System.currentTimeMillis() - date.getTime();

            long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
            long hours = TimeUnit.MILLISECONDS.toHours(diff);
            long days = TimeUnit.MILLISECONDS.toDays(diff);

            if (minutes < 60) return minutes + " minutes ago";
            else if (hours < 24) return hours + " hours ago";
            else return days + " days ago";
        } catch (ParseException e) {
            return "";
        }
    }

    // Filter the entries based on the search query
    private void filterEntries(String query) {
        List<Entry> filteredList = new ArrayList<>();
        for (Entry entry : entries) {
            if (entry.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                    entry.getContent().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(entry);
            }
        }

        filteredEntries.clear();
        filteredEntries.addAll(filteredList); // Update the filtered list
        entryAdapter.notifyDataSetChanged(); // Notify the adapter to update the RecyclerView
    }
}
