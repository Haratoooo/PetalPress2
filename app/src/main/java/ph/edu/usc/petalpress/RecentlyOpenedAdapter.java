package ph.edu.usc.petalpress;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RecentlyOpenedAdapter extends RecyclerView.Adapter<RecentlyOpenedAdapter.ViewHolder> {

    private Context context;
    private List<Journal> journalList;

    public RecentlyOpenedAdapter(Context context, List<Journal> journalList) {
        this.context = context;
        this.journalList = journalList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recent_journal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Journal journal = journalList.get(position);
        holder.titleTextView.setText(journal.getTitle());
        holder.entryCountTextView.setText("Entries: " + journal.getEntryCount());
        holder.imageView.setImageResource(journal.getImageResId());
    }

    @Override
    public int getItemCount() {
        return journalList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleTextView, entryCountTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.journalImage);
            titleTextView = itemView.findViewById(R.id.journalTitle);
            entryCountTextView = itemView.findViewById(R.id.journalEntryCount);
        }
    }
}

