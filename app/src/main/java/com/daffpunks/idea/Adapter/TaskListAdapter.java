package com.daffpunks.idea.Adapter;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daffpunks.idea.Activity.NoteActivity;
import com.daffpunks.idea.Activity.TaskActivity;
import com.daffpunks.idea.DataBase.DatabaseHandler;

import com.daffpunks.idea.Fragment.TaskFragment;
import com.daffpunks.idea.Items.Task;
import com.daffpunks.idea.Listener.DialogListener;
import com.daffpunks.idea.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class TaskListAdapter extends CursorRecyclerAdapter<RecyclerView.ViewHolder> implements DialogListener{
    public final static String EXTRA_ID = "com.daffpunks.idea.ID";

    private final Context           mContext;  // Current context
    private final FragmentManager   mFragmentManager;
    private final TaskListAdapter   thisThing;

    int cardColor;
    int selectedCardColor;

    String cTitle;
    String cDesc;
    String cDate;
    boolean cCompleted;
    int cId;

    private boolean isInSelectionMode;

    private boolean isLocked;

    ArrayList<Task> list = new ArrayList<Task>();

    private List<Long> selectedItems = new ArrayList<>();

    public TaskListAdapter(Context context, Cursor cursor, TaskFragment listFragment) {
        super(cursor);
        this.mContext = context;
        mFragmentManager = listFragment.getFragmentManager();
        thisThing = this;
        cardColor           = 0xffffffff;
        selectedCardColor   = 0xffBBDEFB;
    }



    @Override
    public TaskHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        return new TaskHolder(view);
    }

    @Override
    public void onBindViewHolderCursor(RecyclerView.ViewHolder viewHolder, Cursor cursor) {
        TaskHolder holder = (TaskHolder) viewHolder;
        handleRemind(holder, cursor);
    }

    private boolean isSelected(long id) {
        return selectedItems.contains(id);
    }

    public void unselectAll() {
        selectedItems.clear();
        isInSelectionMode = false;
        broadcastToMain("SELECTIONMODE FALSE");
    }

    public void deleteAllSelectedTask(long id) {
        DatabaseHandler handler = new DatabaseHandler(mContext);
        handler.deleteFromIdTask(id);
        handler.close();
        resetList();
    }

    public void deleteAllSelected() {
        DatabaseHandler handler = new DatabaseHandler(mContext);
        handler.deleteAllTasksFromList(selectedItems);
        handler.close();
        resetList();
    }

    public void toggleComplete(long Id){
        DatabaseHandler handler = new DatabaseHandler(mContext);
        handler.updateCompleted(Id);
        handler.close();
        resetList();
    }

    private void resetList() {
        Intent sendIntent = new Intent("TASKLIST");
        sendIntent.putExtra("action", "TASK ADDED");
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(sendIntent);
    }

    private void toggleSelection(long id, CardView card) {
        if (! isSelected(id)) {
            broadcastToMain("LISTITEM SELECTED");
            selectedItems.add(id);
            card.setCardBackgroundColor(selectedCardColor);
        } else {
            broadcastToMain("LISTITEM UNSELECTED");
            selectedItems.remove(id);
            card.setCardBackgroundColor(cardColor);
        }

        if (selectedItems.size() == 0) {
            broadcastToMain("SELECTIONMODE FALSE");
            isInSelectionMode = false;
        }
    }


    private void broadcastToMain(String message) {
        Intent sendIntent = new Intent("NOTE");
        sendIntent.putExtra("action", message);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(sendIntent);
    }

    @Override
    public Cursor swapCursor(Cursor cursor) {
        super.swapCursor(cursor);
        unselectAll();
        return cursor;
    }

    @Override
    public void onUpdateDialog() {
        Intent sendIntent = new Intent("TASKLIST");
        sendIntent.putExtra("action", "TASK ADDED");
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(sendIntent);
    }

    @Override
    public void onDismissDialog() {
        setIsLocked(false);
    }

    public long getIdfromAdapterPosition(int position){
        return list.get(position).getId();
    }

    public boolean getCompletedfromAdapterPosition(int position){
        return list.get(position).isCompleted();
    }

    private void handleRemind(final TaskHolder holder, final Cursor cursor) {
        cTitle = cursor.getString(1); // title
        cDesc = cursor.getString(2); // desc
        cDate = cursor.getString(3); // date
        int Completed = cursor.getInt(4);
        cCompleted = Completed > 0;
        cId = cursor.getInt(0);

        list.add(new Task(cId, cTitle, cDesc, cDate, cCompleted));

        holder.title.setText(cTitle);
        //ygholder.description.setText(cDesc);
        holder.date.setText(cDate);

        //If description empty, then its invisible

        if(cCompleted)
            holder.status.setBackgroundColor(0xFF4CAF50);
        else
            holder.status.setBackgroundColor(0xFFF44336);


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            holder.card.setRadius(0);
        }

        if(! cDesc.equals("")){
            holder.isComment.setVisibility(View.VISIBLE);
        }else{
            holder.isComment.setVisibility(View.GONE);
        }


        if (isSelected(cId))
            holder.card.setCardBackgroundColor(selectedCardColor);
        else
            holder.card.setCardBackgroundColor(cardColor);

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInSelectionMode)
                    toggleSelection(cId, holder.card);
                else if (!isLocked()) {
                    //setIsLocked(true);
                    Intent intent = new Intent(mContext, TaskActivity.class);
                    int idMessage = cId;
                    intent.putExtra(EXTRA_ID, idMessage);
                    mContext.startActivity(intent);

                    //NoteDialog noteDialog = NoteDialog.newInstance(cId);
                    //noteDialog.show(mFragmentManager,"time_dialog");
                    //noteDialog.setDialogListener(thisThing);
                }
            }

        });

        holder.root.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!isInSelectionMode) {
                    isInSelectionMode = true;
                    broadcastToMain("SELECTIONMODE TRUE");
                    toggleSelection(cId, holder.card);
                }
                return true;
            }
        });
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setIsLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }



    static class TaskHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.cardView)    CardView        card;
        @Bind(R.id.root)        LinearLayout    root;
        @Bind(R.id.title)       TextView        title;
        //@Bind(R.id.desc)      TextView        description;
        @Bind(R.id.date)        TextView        date;
        @Bind(R.id.isComment)   View            isComment;
        @Bind(R.id.status)      View            status;

        public TaskHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }


    }

}


/*
    public void onBindViewHolder(RemindViewHolder holder, int position) {
        Note item = data.get(position);
        holder.title.setText(item.getTitle());
              if( item.getDescription().isEmpty() ){
            holder.description.setHeight(0);
            holder.title.setPadding(0,0,0,0);
        }
        holder.description.setText(item.getDescription());
    }
*/