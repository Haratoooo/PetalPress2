package ph.edu.usc.petalpress;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class JournalDatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "journal.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_JOURNALS = "journals";

    public JournalDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + TABLE_JOURNALS + " (" +
                        "id TEXT PRIMARY KEY, " +
                        "title TEXT, " +
                        "description TEXT, " +
                        "created_at TEXT, " +
                        "image_path TEXT" +
                        ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_JOURNALS);
        onCreate(db);
    }
}
