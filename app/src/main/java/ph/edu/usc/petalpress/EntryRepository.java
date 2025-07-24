package ph.edu.usc.petalpress;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class EntryRepository {
    private EntryDatabaseHelper dbHelper;
    private Context context;

    public EntryRepository(Context context) {
        this.context = context;
        dbHelper = new EntryDatabaseHelper(context);
    }
    public void deleteEntry(String entryId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("entries", "id = ?", new String[]{entryId});
    }


    public List<Entry> getEntriesForJournal(String journalId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("entries", null, "journal_id = ?", new String[]{journalId}, null, null, "created_at DESC");

        List<Entry> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            Entry e = new Entry();
            e.title = cursor.getString(cursor.getColumnIndex("title"));
            e.content = cursor.getString(cursor.getColumnIndex("content"));
            e.createdAt = cursor.getString(cursor.getColumnIndex("created_at"));
            e.mood1 = cursor.getString(cursor.getColumnIndex("mood_1"));
            e.mood2 = cursor.getString(cursor.getColumnIndex("mood_2"));
            e.moodTag = cursor.getString(cursor.getColumnIndex("mood_tag"));

            e.dateLabel = formatDate(e.createdAt);
            e.timeAgo = getTimeAgo(e.createdAt);
            e.mood1ResId = getMoodResId(this.context, e.mood1);
            e.mood2ResId = getMoodResId(this.context, e.mood2);

            list.add(e);
        }

        cursor.close();
        return list;
    }
    public void insertEntry(Entry entry) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", entry.id);
        values.put("journal_id", entry.journalId);
        values.put("title", entry.title);
        values.put("content", entry.content);
        values.put("created_at", entry.createdAt);
        values.put("mood_1", entry.mood1);
        values.put("mood_2", entry.mood2);
        values.put("mood_tag", entry.moodTag);
        db.insert("entries", null, values);
    }


    private int getMoodResId(Context context, String name) {
        return context.getResources().getIdentifier(name, "drawable", context.getPackageName());
    }

    public void deleteEntriesByJournal(String journalId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("entries", "journal_id = ?", new String[]{journalId});
    }

    private String formatDate(String rawDate) {
        try {
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = parser.parse(rawDate);
            SimpleDateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
            return formatter.format(date);
        } catch (Exception e) {
            return "";
        }
    }

    private String getTimeAgo(String rawDate) {
        try {
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = parser.parse(rawDate);
            long diff = System.currentTimeMillis() - date.getTime();

            long mins = TimeUnit.MILLISECONDS.toMinutes(diff);
            long hours = TimeUnit.MILLISECONDS.toHours(diff);
            long days = TimeUnit.MILLISECONDS.toDays(diff);

            if (mins < 60) return mins + " minutes ago";
            else if (hours < 24) return hours + " hours ago";
            else return days + " days ago";
        } catch (Exception e) {
            return "";
        }
    }
}
