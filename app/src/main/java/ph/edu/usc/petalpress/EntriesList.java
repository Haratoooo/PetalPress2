package ph.edu.usc.petalpress;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
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



        // 🔍 Log received Intent values
        String journalTitleStr = getIntent().getStringExtra("journal_title");
        String description = getIntent().getStringExtra("journal_description");
        int imageResId = getIntent().getIntExtra("journal_image", -1);
        String journalId = getIntent().getStringExtra("journal_id");

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
        entryAdapter = new EntryAdapter(entries);
        entryRecyclerView.setAdapter(entryAdapter);

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
                    entryAdapter.notifyDataSetChanged();
                    Log.d(TAG, "Fetched " + fetchedEntries.size() + " entries.");
                });

            } catch (Exception e) {
                Log.e(TAG, "Error parsing entries", e);
            }
        }).start();
    }

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


}

