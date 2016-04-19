package com.daffpunks.idea.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.daffpunks.idea.Items.Note;
import com.daffpunks.idea.Items.Task;

import java.util.List;

/**
 * Created by User on 13.04.2016.
 */
public class DatabaseHandler extends SQLiteOpenHelper{

    public static final String TABLE_NOTE    = "noteTable";
    public static final String TABLE_TASK    = "taskTable";

    public static final String KEY_ID        = "_id";
    public static final String KEY_TXT       = "title";
    public static final String KEY_DESC      = "description";
    public static final String KEY_DATE      = "date";
    public static final String KEY_COMPLETED = "completed";

    // Database Version
    private static final int DB_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "myDataBase";

    private static final String DB_NOTE_CREATE =
            "CREATE TABLE " + TABLE_NOTE + "("
                    + KEY_ID   + " INTEGER PRIMARY KEY, "
                    + KEY_TXT  + " TEXT,"
                    + KEY_DESC + " TEXT,"
                    + KEY_DATE + " TEXT "
                    + ");";

    private static final String DB_TASK_CREATE =
            "CREATE TABLE " + TABLE_TASK + "("
                    + KEY_ID        + " INTEGER PRIMARY KEY, "
                    + KEY_TXT       + " TEXT,"
                    + KEY_DESC      + " TEXT,"
                    + KEY_DATE      + " TEXT, "
                    + KEY_COMPLETED + " INTEGER "
                    + ");";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_NOTE_CREATE);
        db.execSQL(DB_TASK_CREATE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void addNote(Note note) {
        if(!note.getTitle().equals("")) {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put( KEY_TXT,  note.getTitle());
            values.put( KEY_DESC, note.getDescription());
            values.put( KEY_DATE, note.getDate());

            db.insert(TABLE_NOTE, null, values);
            db.close();
        }
    }

    public void addTask(Task task) {
        if(!task.getTitle().equals("")) {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put( KEY_TXT,       task.getTitle());
            values.put( KEY_DESC,      task.getDescription());
            values.put( KEY_DATE,      task.getDate());
            values.put( KEY_COMPLETED, (task.isCompleted()) ? 1 : 0);

            db.insert(TABLE_TASK, null, values);
            db.close();
        }
    }

    public int updateNoteWithId(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TXT, note.getTitle());
        values.put(KEY_DESC, note.getDescription());
        values.put(KEY_DATE, note.getDate());

        // Updating row
        return db.update(TABLE_NOTE, values, KEY_ID + " = ?",
                new String[] { String.valueOf(note.getId()) });
    }

    public int updateTaskWithId(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TXT,       task.getTitle());
        values.put(KEY_DESC,      task.getDescription());
        values.put(KEY_DATE,      task.getDate());
        values.put(KEY_COMPLETED, task.isCompleted());

        // Updating row
        return db.update(TABLE_TASK, values, KEY_ID + " = ?",
                new String[] { String.valueOf(task.getId()) });
    }

    public int updateCompleted(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        Cursor cursor = db.query(TABLE_TASK, new String[] { KEY_ID, KEY_TXT, KEY_DESC, KEY_DATE, KEY_COMPLETED }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        int dayz = cursor.getInt(4);

        if(dayz == 0) {
            values.put(KEY_COMPLETED, 1);
        }
        else {
            values.put(KEY_COMPLETED, 0);
        }

        // Updating row
        return db.update(TABLE_TASK, values, KEY_ID + " = ?",
                new String[] { String.valueOf( id ) });
    }

    public Note getNote(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NOTE, new String[] { KEY_ID, KEY_TXT, KEY_DESC, KEY_DATE }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Note note = new Note(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3));

        // Return solve
        cursor.close();
        db.close();
        return note;
    }

    public Task getTask(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TASK, new String[] { KEY_ID, KEY_TXT, KEY_DESC, KEY_DATE, KEY_COMPLETED }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Task task = new Task(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getInt(4) > 0);

        // Return solve
        cursor.close();
        db.close();
        return task;
    }

    // Delete an entry with an id
    public void deleteFromIdTask(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASK, KEY_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    // Delete entries with an id list
    public void deleteAllNotesFromList(List<Long> idList) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (int i = 0; i < idList.size(); i++) {
            db.delete(TABLE_NOTE, KEY_ID + " = ?", new String[] { Long.toString(idList.get(i)) });
        }
        db.close();
    }

    public void deleteAllTasksFromList(List<Long> idList) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (int i = 0; i < idList.size(); i++) {
            db.delete(TABLE_TASK, KEY_ID + " = ?", new String[] { Long.toString(idList.get(i)) });
        }
        db.close();
    }






    /*
    public void open() {
        mDBHelper = new DBHelper(mCtx, DATABASE_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();
    }

    public void close() {
        if (mDBHelper!=null) mDBHelper.close();
    }
    public Cursor getAllData() {
        return mDB.query(TABLE_NOTE, null, null, null, null, null, null);
    }

    public void addRec(String txt, String desc , String date) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_TXT, txt);
        cv.put(KEY_DESC, desc);
        cv.put(KEY_DATE, date);
        mDB.insert(TABLE_NOTE, null, cv);
    }

    public void delRec(long id) {
        mDB.delete(TABLE_NOTE, KEY_ID + " = " + id, null);
    }


    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE);

            ContentValues cv = new ContentValues();
            for (int i = 1; i < 5; i++) {
                cv.put(KEY_TXT, "sometext " + i);
                cv.put(KEY_DESC, "sometext " + i);
                cv.put(KEY_DATE, "13/04");
                db.insert(TABLE_NOTE, null, cv);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
    */
}
