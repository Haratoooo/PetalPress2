package ph.edu.usc.petalpress;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

public class RecentlyOpenedAdapter extends RecyclerView.Adapter<RecentlyOpenedAdapter.ViewHolder> {

    private Context context;
    private List<Journal> journalList;

    public RecentlyOpenedAdapter(Context context, List<Journal> journalList) {
        this.context = context;
        this.journalList = journalList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recent_journal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Journal journal = journalList.get(position);

        holder.titleTextView.setText(journal.title);
        holder.entryCountTextView.setText("Created: " + journal.createdAt);

        if (journal.imagePath != null && new File(journal.imagePath).exists()) {
            holder.imageView.setImageBitmap(BitmapFactory.decodeFile(journal.imagePath));
        } else {
            holder.imageView.setImageResource(R.drawable.homepage_background); // fallback image
        }

        holder.itemView.setOnClickListener(v -> {
            Log.d("RecentlyOpenedAdapter", "Sending intent - title: " + journal.title +
                    ", desc: " + journal.description +
                    ", imagePath: " + journal.imagePath);

            Intent intent = new Intent(context, EntriesList.class);
            intent.putExtra("journal_id", journal.id);
            intent.putExtra("journal_title", journal.title);
            intent.putExtra("journal_description", journal.description);
            intent.putExtra("journal_image_path", journal.imagePath);

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return journalList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleTextView, entryCountTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.journalImage);
            titleTextView = itemView.findViewById(R.id.journalTitle);
            entryCountTextView = itemView.findViewById(R.id.journalEntryCount);
        }
    }
}
