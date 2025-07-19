package ph.edu.usc.petalpress;

import android.content.Context;
import org.json.JSONException;
import org.json.JSONObject;

public class Journal {
    private String title;
    private int entryCount;
    private int imageResId;

    public Journal(String title, int entryCount, int imageResId) {
        this.title = title;
        this.entryCount = entryCount;
        this.imageResId = imageResId;
    }

    public static Journal fromJson(Context context, JSONObject obj) throws JSONException {
        String title = obj.getString("title");
        int entryCount = obj.getInt("entryCount");
        String imageName = obj.getString("imageName");

        int imageResId = context.getResources().getIdentifier(
                imageName, "drawable", context.getPackageName());

        return new Journal(title, entryCount, imageResId);
    }

    public String getTitle() { return title; }
    public int getEntryCount() { return entryCount; }
    public int getImageResId() { return imageResId; }
}
