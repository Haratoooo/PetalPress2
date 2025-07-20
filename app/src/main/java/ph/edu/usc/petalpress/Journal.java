package ph.edu.usc.petalpress;

import android.content.Context;
import org.json.JSONException;
import org.json.JSONObject;

public class Journal {
    private String id;
    private String title;
    private int entryCount;
    private int imageResId;
    private String createdAt;

    public Journal(String id, String title, int entryCount, int imageResId, String createdAt) {
        this.id = id;
        this.title = title;
        this.entryCount = entryCount;
        this.imageResId = imageResId;
        this.createdAt = createdAt;
    }

    public static Journal fromJson(Context context, JSONObject obj) throws JSONException {
        String id = obj.getString("id");
        String title = obj.getString("title");
        int entryCount = obj.getInt("entry_count");  // match Supabase field
        String imageName = obj.getString("image_name");
        String createdAt = obj.optString("created_at", "");  // optional to prevent crash

        int imageResId = context.getResources().getIdentifier(
                imageName, "drawable", context.getPackageName());

        return new Journal(id, title, entryCount, imageResId, createdAt);
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public int getEntryCount() { return entryCount; }
    public int getImageResId() { return imageResId; }
    public String getCreatedAt() { return createdAt; }

}
