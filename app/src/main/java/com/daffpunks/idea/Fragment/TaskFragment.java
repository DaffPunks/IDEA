package com.daffpunks.idea.Fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.LoaderManager;
import android.content.Context;
import android.support.v4.content.ContextCompat;
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
import com.daffpunks.idea.Adapter.TaskListAdapter;
import com.daffpunks.idea.DataBase.DatabaseHandler;
import com.daffpunks.idea.Items.Task;
import com.daffpunks.idea.Loader.TaskCursorLoader;
import com.daffpunks.idea.MainActivity;
import com.daffpunks.idea.R;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TaskFragment extends AbstractTabFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final int LAYOUT = R.layout.fragment_note;

    private static final int TASK_LOADER_ID = 1;

    private TaskListAdapter     taskListAdapter;
    private TaskCursorLoader    taskCursorLoader;

    @Bind(R.id.list)        RecyclerView            recyclerView;
    @Bind(R.id.fab)         FloatingActionButton    floatingActionButton;

    private Context mContext;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isAdded()) { // The fragment has to check if it is attached to an activity. Removing this will bug the app
                switch (intent.getStringExtra("action")) {
                    case "TASK ADDED":
                        resetList();
                        break;
                    case "UNSELECT ALL":
                        resetList();
                        break;
                    case "DELETE SELECTED":
                        taskListAdapter.deleteAllSelected();
                        break;
                }
            }
        }
    };

    public TaskFragment() {
        // Required empty public constructor
    }

    public static TaskFragment getInstance(Context context){
        TaskFragment fragment = new TaskFragment();
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

                                handler.addTask(new Task(editTitle.getText().toString(), editText.getText().toString(), date, false));

                                Intent sendIntent = new Intent("TASKLIST");
                                sendIntent.putExtra("action", "TASK ADDED");
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

        LocalBroadcastManager.getInstance(mContext).registerReceiver(mReceiver, new IntentFilter("TASKLIST"));

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            Drawable    background;
            Drawable    xMark;
            int         xMarkMargin;
            boolean     initiated;

            private void init() {

                //backgroundRed = new ColorDrawable(0xFFF44336);
                //backgroundGreen = new ColorDrawable(0xFF4CAF50);
                xMark = ContextCompat.getDrawable(mContext, R.drawable.ic_cached_black_24dp);
                xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                xMarkMargin = (int) TaskFragment.this.getResources().getDimension(R.dimen.ic_clear_margin);
                initiated = true;
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {

                //taskListAdapter.deleteAllSelectedTask(taskListAdapter.getIdfromAdapterPosition(viewHolder.getAdapterPosition()));
                taskListAdapter.toggleComplete(taskListAdapter.getIdfromAdapterPosition(viewHolder.getAdapterPosition()));



                taskListAdapter.notifyItemChanged(viewHolder.getAdapterPosition());

                Snackbar.make(floatingActionButton, "Done", Snackbar.LENGTH_LONG)
                //        .setAction("UNDO", new View.OnClickListener() {
                //            @Override
                //            public void onClick(View view) {
                //                list.add(tmpIndx, tmpRmnd);
                //                adapter.notifyItemInserted(tmpIndx);
                //            }
                        .show();

            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;

                // not sure why, but this method get's called for viewholder that are already swiped away
                if (viewHolder.getAdapterPosition() == -1) return;
                if (!initiated) init();

                if(taskListAdapter.getCompletedfromAdapterPosition(viewHolder.getAdapterPosition()))
                    background = new ColorDrawable(0xFFF44336);
                else
                    background = new ColorDrawable(0xFF4CAF50);
                // draw red background
                background.setBounds(itemView.getRight() + (int) dX, itemView.getTop() + 10, itemView.getRight() - 10, itemView.getBottom() - 10);
                background.draw(c);

                // draw x mark
                int itemHeight = itemView.getBottom() - itemView.getTop();
                int intrinsicWidth = xMark.getIntrinsicWidth();
                int intrinsicHeight = xMark.getIntrinsicWidth();
                int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
                int xMarkRight = itemView.getRight() - xMarkMargin;
                int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight)/2;
                int xMarkBottom = xMarkTop + intrinsicHeight;
                xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);
                xMark.draw(c);
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);


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
        taskCursorLoader = new TaskCursorLoader(mContext);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return taskCursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        taskListAdapter.swapCursor(cursor);
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        taskListAdapter.swapCursor(null);
    }



    private void setupRecyclerView() {
        taskListAdapter = new TaskListAdapter(getActivity(), null, this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setAdapter(taskListAdapter);

    }
}
