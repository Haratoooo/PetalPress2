package ph.edu.usc.petalpress;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Homepage extends AppCompatActivity {

    private RecyclerView recentlyOpenedRecyclerView;
    private RecentlyOpenedAdapter adapter;
    private List<Journal> journalList;

    private ViewFlipper journalFlipper;
    private ImageView arrowLeft, arrowRight;
    private TextView pageIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        // Setup Recently Opened RecyclerView
        recentlyOpenedRecyclerView = findViewById(R.id.recentlyOpenedRecyclerView);
        recentlyOpenedRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );

        journalList = loadJournalsFromJson();
        adapter = new RecentlyOpenedAdapter(this, journalList);
        recentlyOpenedRecyclerView.setAdapter(adapter);

        // Setup ViewFlipper for 'Journals You Created'
        journalFlipper = findViewById(R.id.journalFlipper);
        arrowLeft = findViewById(R.id.arrowLeft);
        arrowRight = findViewById(R.id.arrowRight);
        pageIndicator = findViewById(R.id.pageIndicator);

        LayoutInflater inflater = LayoutInflater.from(this);
        View page1 = inflater.inflate(R.layout.journal_page_1, journalFlipper, false);
        View page2 = inflater.inflate(R.layout.journal_page_2, journalFlipper, false);

        journalFlipper.addView(page1);
        journalFlipper.addView(page2);

        updatePageIndicator();

        arrowLeft.setOnClickListener(v -> {
            journalFlipper.showPrevious();
            updatePageIndicator();
        });

        arrowRight.setOnClickListener(v -> {
            journalFlipper.showNext();
            updatePageIndicator();
        });
    }

    private void updatePageIndicator() {
        int current = journalFlipper.getDisplayedChild() + 1;
        int total = journalFlipper.getChildCount();
        pageIndicator.setText(current + " / " + total);
    }

    private List<Journal> loadJournalsFromJson() {
        List<Journal> journals = new ArrayList<>();
        try {
            InputStream is = getResources().openRawResource(R.raw.recent_journals);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);

            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                journals.add(Journal.fromJson(this, obj));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return journals;
    }
}
