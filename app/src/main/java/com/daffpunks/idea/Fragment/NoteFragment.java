package com.daffpunks.idea.Fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.support.v4.app.LoaderManager;
import android.content.Context;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.daffpunks.idea.Adapter.RemindListAdapter;
import com.daffpunks.idea.Items.Note;
import com.daffpunks.idea.DataBase.DatabaseHandler;
import com.daffpunks.idea.Loader.NoteCursorLoader;
import com.daffpunks.idea.R;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NoteFragment extends AbstractTabFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final int LAYOUT = R.layout.fragment_note;

    private static final int TASK_LOADER_ID = 0;

    private RemindListAdapter   remindListAdapter;
    private NoteCursorLoader    noteCursorLoader;

    @Bind(R.id.list)        RecyclerView            recyclerView;
    @Bind(R.id.fab)         FloatingActionButton    floatingActionButton;

    private Context mContext;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isAdded()) { // The fragment has to check if it is attached to an activity. Removing this will bug the app
                switch (intent.getStringExtra("action")) {
                    case "NOTE ADDED":
                        resetList();
                        break;
                    case "UNSELECT ALL":
                        resetList();
                        break;
                    case "DELETE SELECTED":
                        remindListAdapter.deleteAllSelected();
                        break;
                }
            }
        }
    };

    public NoteFragment() {
        // Required empty public constructor
    }

    public static NoteFragment getInstance(Context context){
        NoteFragment fragment = new NoteFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.setTitle(context.getString(R.string.tab_item_notes));
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)  {
        rootView = inflater.inflate(LAYOUT,container,false);
        ButterKnife.bind(this, rootView);
        mContext = getActivity().getApplicationContext();



        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(getContext())
                        .title("New note")
                        .theme(Theme.LIGHT)
                        .customView(R.layout.dialog_input, false)
                        .positiveText("Ok")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                DatabaseHandler handler = new DatabaseHandler(getContext());

                                View view = dialog.getCustomView();
                                EditText editTitle = (EditText) view.findViewById(R.id.editTitle);
                                EditText editText = (EditText) view.findViewById(R.id.editText);
                                Calendar calendar = Calendar.getInstance();
                                int day = calendar.get(Calendar.DAY_OF_MONTH);
                                int month = calendar.get(Calendar.MONTH);
                                int hours = calendar.get(Calendar.HOUR);
                                int minutes = calendar.get(Calendar.MINUTE);
                                String date = day + "/0" + month + "  " + hours + ":" + minutes;

                                handler.addNote(new Note(editTitle.getText().toString(), editText.getText().toString(), date));

                                Intent sendIntent = new Intent("TIMELIST");
                                sendIntent.putExtra("action", "NOTE ADDED");
                                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(sendIntent);

                                //list.add(new Note(editTitle.getText().toString(), editText.getText().toString()));
                                //db.addRec(editTitle.getText().toString(),editText.getText().toString(),"13/04");
                                // получаем новый курсор с данными
                                //getLoaderManager().getLoader(0).forceLoad();
                                //adapter.notifyItemInserted(adapter.getItemCount());

                                editTitle.setText("");
                                editText.setText("");
                            }
                        })
                        .negativeText("Cancel")
                        .show();
            }
        });

        setupRecyclerView();

        getTaskLoader();

        getLoaderManager().initLoader(TASK_LOADER_ID, null, this);

        LocalBroadcastManager.getInstance(mContext).registerReceiver(mReceiver, new IntentFilter("TIMELIST"));





        /*
        db = new DatabaseHandler(context);
        db.open();

        registerForContextMenu(recyclerView);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        noteCursorLoader = new NoteCursorLoader(context,db);

        getLoaderManager().initLoader(0, null, this);

        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(this);
        */

        /*
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                //db.swapRec(adapter.getId(), adapter.getId() + 1);
                //Collections.swap(list, viewHolder.getAdapterPosition(), target.getAdapterPosition());
                //adapter.notifyItemMoved(adapter.getId(), adapter.getId() + 1);
                //getLoaderManager().getLoader(0).forceLoad();
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //tmpRmnd = list.get(viewHolder.getAdapterPosition());
                //tmpIndx = ;

                viewHolder.getAdapterPosition();
                //db.delRec(adapter.getId());
                //adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                //getLoaderManager().getLoader(0).forceLoad();

                Snackbar.make(floatingActionButton, viewHolder.getAdapterPosition() + " " + viewHolder.getOldPosition() + " " + viewHolder.getItemViewType() + " " + viewHolder.getLayoutPosition() + " " + viewHolder.getItemId(), Snackbar.LENGTH_LONG)
                //        .setAction("UNDO", new View.OnClickListener() {
                //            @Override
                //            public void onClick(View view) {
                //                list.add(tmpIndx, tmpRmnd);
                //                adapter.notifyItemInserted(tmpIndx);
                //            }
                        .show();

            }

        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        */

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // To fix memory leaks
        ButterKnife.unbind(this);
        getLoaderManager().destroyLoader(1);
    }

    public void resetList() {
        getTaskLoader();
        getLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
    }


    private void getTaskLoader() {
        noteCursorLoader = new NoteCursorLoader(mContext);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return noteCursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        remindListAdapter.swapCursor(cursor);
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        remindListAdapter.swapCursor(null);
    }



    private void setupRecyclerView() {
        remindListAdapter = new RemindListAdapter(getActivity(), null, this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setAdapter(remindListAdapter);

    }
}
