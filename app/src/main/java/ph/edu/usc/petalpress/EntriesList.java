package ph.edu.usc.petalpress;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class EntriesList extends AppCompatActivity {

    private RecyclerView entryRecyclerView;
    private EntryAdapter entryAdapter;
    private List<Entry> entries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entries_list); // match your layout file

        entryRecyclerView = findViewById(R.id.entryRecyclerView);
        entryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Dummy data for now
        entries = new ArrayList<>();
        entries.add(new Entry("Coffee & Clarity", "Yesterday, 23 Apr. 2025", "16 hours ago",
                R.drawable.ic_mood_yellow, R.drawable.ic_mood_green));
        entries.add(new Entry("Woke Up With A Thought", "April 22, 2025", "1 day ago",
                R.drawable.ic_mood_yellow, R.drawable.ic_mood_green));

        entryAdapter = new EntryAdapter(entries);
        entryRecyclerView.setAdapter(entryAdapter);
    }
}
