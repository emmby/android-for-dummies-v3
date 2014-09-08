package com.dummies.tasks.adapter;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dummies.tasks.R;
import com.dummies.tasks.fragment.TaskEditFragment;
import com.dummies.tasks.interfaces.OnEditTask;
import com.dummies.tasks.provider.TaskProvider;
import com.dummies.tasks.view.TaskCard;
import com.squareup.picasso.Picasso;

public class TaskListAdapter
        extends RecyclerView.Adapter<TaskListAdapter.ViewHolder> {
    Cursor cursor;
    int titleColumnIndex;
    int notesColumnIndex;
    int idColumnIndex;


    public void swapCursor(Cursor c) {
        cursor = c;
        if(c!=null) {
            cursor.moveToFirst();
            titleColumnIndex = cursor.getColumnIndex(TaskProvider.COLUMN_TITLE);
            notesColumnIndex = cursor.getColumnIndex(TaskProvider.COLUMN_NOTES);
            idColumnIndex = cursor.getColumnIndex(TaskProvider.COLUMN_TASKID);
        }
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        // create a new view
        TaskCard v = new TaskCard(parent.getContext());

        // wrap it in a ViewHolder
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final long id = getItemId(i);
        final Context context = viewHolder.titleView.getContext();

        // set the text
        cursor.moveToPosition(i);
        viewHolder.titleView.setText(cursor.getString(titleColumnIndex));
        viewHolder.notesView.setText(cursor.getString(notesColumnIndex));

        // set the thumbnail image
        Picasso.with(context)
                .load(TaskEditFragment.getImageUrlForTask(id) )
                .into(viewHolder.imageView);

        // Set the click action
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((OnEditTask) context).editTask(id);
            }
        });

        viewHolder.cardView.setOnLongClickListener( new View
                .OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                viewHolder.cardView.animateCardUp();
                new AlertDialog.Builder(context)
                        .setTitle(R.string.delete_q)
                        .setMessage(viewHolder.titleView.getText())
                        .setCancelable(true)
                        .setNegativeButton(android.R.string.cancel,null)
                        .setPositiveButton(R.string.delete,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface
                                                        dialogInterface,
                                                        int i) {
                                        deleteTask(context,id);
                                    }
                                })
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                viewHolder.cardView.animateCardDown();
                            }
                        })
                        .show();
                return true;
            }
        });

    }

    private void deleteTask(Context context, long taskId) {
        context.getContentResolver()
                .delete(
                        ContentUris.withAppendedId(
                                TaskProvider.CONTENT_URI,
                                taskId),
                        null, null);
    }

    @Override
    public long getItemId(int position) {
        cursor.moveToPosition(position);
        return cursor.getLong(idColumnIndex);
    }

    @Override
    public int getItemCount() {
        return cursor!=null ? cursor.getCount() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TaskCard cardView;
        TextView titleView;
        TextView notesView;
        ImageView imageView;

        public ViewHolder(TaskCard itemView) {
            super(itemView);
            cardView = itemView;
            titleView = (TextView) itemView.findViewById(R.id.text1);
            notesView = (TextView) itemView.findViewById(R.id.text2);
            imageView = (ImageView) itemView.findViewById(R.id.image);
        }

    }
}
