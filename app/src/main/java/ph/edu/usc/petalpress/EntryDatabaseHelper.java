package ph.edu.usc.petalpress;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class EntryDatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "entry.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_ENTRIES = "entries";

    public EntryDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + TABLE_ENTRIES + " (" +
                        "id TEXT PRIMARY KEY, " +
                        "journal_id TEXT, " +
                        "title TEXT, " +
                        "content TEXT, " +
                        "created_at TEXT, " +
                        "mood_1 TEXT, " +
                        "mood_2 TEXT, " +
                        "mood_tag TEXT" +
                        ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENTRIES);
        onCreate(db);
    }
}
