package com.daffpunks.idea.Loader;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.CursorLoader;
import android.database.Cursor;

import com.daffpunks.idea.DataBase.DatabaseHandler;

/**
 * Created by User on 13.04.2016.
 */
public class TaskCursorLoader extends CursorLoader {

    private DatabaseHandler handler;
    private SQLiteDatabase  db;

    public TaskCursorLoader(Context context) {
        super(context);
    }

    @Override
    public Cursor loadInBackground() {
        handler = new DatabaseHandler(getContext());
        db = handler.getReadableDatabase();

        return db.query(DatabaseHandler.TABLE_TASK, null, null, null, null, null, null, null);
    }

    @Override
    protected void onStopLoading() {
        //db.close();
        //handler.close();
        super.onStopLoading();
    }
}
