package ph.edu.usc.petalpress;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class EntryStartActivity extends AppCompatActivity {

    private EditText titleInput;
    private Button moodHappy, moodSad, moodAdd, continueButton;
    private LinearLayout moodBox;
    private Set<Button> selectedMoodButtons = new HashSet<>();
    private List<String> selectedMoods = new ArrayList<>();
    private TextInputEditText thoughtsInput;

    private String userToken; // Must be passed/stored from login
    private String userId;    // Same here
    private String journalId; // Passed from previous activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_page);

        // Get token and journalId from intent or SharedPreferences
        userToken = getIntent().getStringExtra("user_token");
        journalId = getIntent().getStringExtra("journal_id");
        userId = getIntent().getStringExtra("user_id"); // Must be saved from login

        // Bind views
        titleInput = findViewById(R.id.titleInput);
        moodHappy = findViewById(R.id.moodHappy);
        moodSad = findViewById(R.id.moodSad);
        moodAdd = findViewById(R.id.moodAdd);
        moodBox = findViewById(R.id.moodBox);
        continueButton = findViewById(R.id.continueButton);
        ImageView backButton = findViewById(R.id.backButton);


        setupMoodToggle(moodHappy, "Happy");
        setupMoodToggle(moodSad, "Sad");

        moodAdd.setOnClickListener(v -> showCustomMoodDialog());

        continueButton.setOnClickListener(v -> {
            String title = titleInput.getText().toString().trim();
            String content = thoughtsInput.getText().toString().trim();

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "Please complete all fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            insertEntryLocally(title, content);

        });

        backButton.setOnClickListener(v -> onBackPressed());
    }

    private void setupMoodToggle(Button button, String moodText) {
        button.setOnClickListener(v -> {
            if (selectedMoodButtons.contains(button)) {
                button.setBackgroundResource(R.drawable.pill_button);
                selectedMoodButtons.remove(button);
                selectedMoods.remove(moodText);
            } else {
                button.setBackgroundColor(Color.parseColor("#FFE4B5")); // Highlight
                selectedMoodButtons.add(button);
                selectedMoods.add(moodText);
            }
        });
    }

    private void showCustomMoodDialog() {
        final EditText input = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("Add Mood")
                .setMessage("Enter custom mood:")
                .setView(input)
                .setPositiveButton("Add", (dialog, which) -> {
                    String mood = input.getText().toString().trim();
                    if (!mood.isEmpty()) {
                        Button newMood = new Button(this);
                        newMood.setText(mood);
                        newMood.setBackgroundResource(R.drawable.pill_button);
                        newMood.setTextColor(Color.BLACK);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT, 100);
                        params.setMargins(8, 16, 8, 8);
                        newMood.setLayoutParams(params);
                        moodBox.addView(newMood, moodBox.getChildCount() - 1);
                        setupMoodToggle(newMood, mood);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void insertEntryLocally(String title, String content) {
        String entryId = UUID.randomUUID().toString();
        String createdAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        String mood1 = selectedMoods.size() > 0 ? selectedMoods.get(0).toLowerCase() : "neutral";
        String mood2 = selectedMoods.size() > 1 ? selectedMoods.get(1).toLowerCase() : "neutral";
        String moodTag = String.join(", ", selectedMoods);

        Entry entry = new Entry();
        entry.id = entryId;
        entry.journalId = journalId;
        entry.title = title;
        entry.content = content;
        entry.createdAt = createdAt;
        entry.mood1 = mood1;
        entry.mood2 = mood2;
        entry.moodTag = moodTag;

        EntryRepository repo = new EntryRepository(this);
        repo.insertEntry(entry);

        Toast.makeText(this, "Entry saved locally ✨", Toast.LENGTH_SHORT).show();

        // Navigate back to EntriesList
        Intent intent = new Intent(this, EntriesList.class);
        intent.putExtra("journal_id", journalId);
        intent.putExtra("journal_title", getIntent().getStringExtra("journal_title"));
        intent.putExtra("journal_description", getIntent().getStringExtra("journal_description"));
        intent.putExtra("journal_image_path", getIntent().getStringExtra("journal_image_path"));
        startActivity(intent);
        finish();
    }

}

