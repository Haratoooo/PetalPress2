package ph.edu.usc.petalpress;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private final List<NotificationItem> list;

    public NotificationAdapter(List<NotificationItem> list) {
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        NotificationItem item = list.get(position);
        holder.title.setText(item.getTitle());
        holder.description.setText(item.getDescription());
        holder.timestamp.setText(item.getTimestamp());
        holder.icon.setImageResource(R.drawable.ic_bell);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title, description, timestamp;

        ViewHolder(View itemView) {
            super(itemView);
            icon        = itemView.findViewById(R.id.notificationIcon);
            title       = itemView.findViewById(R.id.notificationTitle);
            description = itemView.findViewById(R.id.notificationDescription);
            timestamp   = itemView.findViewById(R.id.notificationTimestamp);
        }
    }
}