package ph.edu.usc.petalpress;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class JournalRepository {
    private JournalDatabaseHelper dbHelper;

    public JournalRepository(Context context) {
        dbHelper = new JournalDatabaseHelper(context);
    }

    public void insertJournal(String id, String title, String description, String createdAt, String imagePath) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("title", title);
        values.put("description", description);
        values.put("created_at", createdAt);
        values.put("image_path", imagePath);
        db.insert(JournalDatabaseHelper.TABLE_JOURNALS, null, values);
    }

    public ArrayList<Journal> getAllJournals() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(JournalDatabaseHelper.TABLE_JOURNALS, null, null, null, null, null, "created_at DESC");
        ArrayList<Journal> list = new ArrayList<>();

        while (cursor.moveToNext()) {
            Journal journal = new Journal();
            journal.id = cursor.getString(cursor.getColumnIndex("id"));
            journal.title = cursor.getString(cursor.getColumnIndex("title"));
            journal.description = cursor.getString(cursor.getColumnIndex("description"));
            journal.createdAt = cursor.getString(cursor.getColumnIndex("created_at"));
            journal.imagePath = cursor.getString(cursor.getColumnIndex("image_path"));
            list.add(journal);
        }

        cursor.close();
        return list;
    }

    public void deleteJournal(String id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(JournalDatabaseHelper.TABLE_JOURNALS, "id=?", new String[]{id});
    }
}
