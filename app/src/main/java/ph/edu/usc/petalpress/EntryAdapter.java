package ph.edu.usc.petalpress;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.EntryViewHolder> {

    private List<Entry> entryList;

    public EntryAdapter(List<Entry> entryList) {
        this.entryList = entryList;
    }

    public static class EntryViewHolder extends RecyclerView.ViewHolder {
        TextView entryTitle, entryDateLabel, entryTimeAgo;
        ImageView moodTag1, moodTag2;

        public EntryViewHolder(View itemView) {
            super(itemView);
            entryTitle = itemView.findViewById(R.id.entryTitle);
            entryDateLabel = itemView.findViewById(R.id.entryDateLabel);
            entryTimeAgo = itemView.findViewById(R.id.entryTimeAgo);
            moodTag1 = itemView.findViewById(R.id.moodTag1);
            moodTag2 = itemView.findViewById(R.id.moodTag2);
        }
    }

    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.entry_item_layout, parent, false);
        return new EntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
        Entry entry = entryList.get(position);
        holder.entryTitle.setText(entry.getTitle());
        holder.entryDateLabel.setText(entry.getDateLabel());
        holder.entryTimeAgo.setText(entry.getTimeAgo());
        holder.moodTag1.setImageResource(entry.getMood1ResId());
        holder.moodTag2.setImageResource(entry.getMood2ResId());
    }

    @Override
    public int getItemCount() {
        return entryList.size();
    }
}
