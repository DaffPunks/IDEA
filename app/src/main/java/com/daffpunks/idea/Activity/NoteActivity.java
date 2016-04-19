package com.daffpunks.idea.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.daffpunks.idea.DataBase.DatabaseHandler;
import com.daffpunks.idea.Fragment.NoteFragment;
import com.daffpunks.idea.Items.Note;
import com.daffpunks.idea.Listener.DialogListener;
import com.daffpunks.idea.MainActivity;
import com.daffpunks.idea.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NoteActivity extends AppCompatActivity {
    public final static String EXTRA_TITLE = "com.daffpunks.idea.TITLE";
    public final static String EXTRA_DESCRIPTION = "com.daffpunks.idea.DESCRIPTION";
    public final static String EXTRA_ID = "com.daffpunks.idea.ID";
    public final static String EXTRA_DATE = "com.daffpunks.idea.DATE";

    private Toolbar toolbar;
    private DialogListener  dialogListener;
    private DatabaseHandler handler;
    private SQLiteDatabase db;
    private int ID;
    private String title;
    private String description;
    private String date;

    @Bind(R.id.titlenote)   EditText    titleView;
    @Bind(R.id.descnote)    EditText    descView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        ButterKnife.bind(this);

        Intent intent = getIntent();

        handler = new DatabaseHandler(this);
        db = handler.getWritableDatabase();

        ID = intent.getIntExtra(EXTRA_ID, -1);

        initToolbar();

        Note note = handler.getNote(ID);

        titleView.setText(note.getTitle());
        descView.setText(note.getDescription());
        date = note.getDate();
    }


    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_back_white_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.inflateMenu(R.menu.check_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                title = titleView.getText().toString();
                description = descView.getText().toString();
                handler.updateNoteWithId(new Note(ID,title,description,date));

                Intent sendIntent = new Intent("TIMELIST");
                sendIntent.putExtra("action", "NOTE ADDED");
                LocalBroadcastManager.getInstance(NoteActivity.this).sendBroadcast(sendIntent);

                finish();
                return false;
            }
        });
    }

    public void setDialogListener(DialogListener listener) {
        dialogListener = listener;
    }

    @Override
    protected void onDestroy() {
        db.close();
        handler.close();
        ButterKnife.unbind(this);
        if (dialogListener != null)
            dialogListener.onDismissDialog();
        super.onDestroy();
    }
}
