package com.dummies.tasks.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.TextView;

import com.dummies.tasks.R;
import com.dummies.tasks.activity.TaskPreferencesActivity;
import com.dummies.tasks.interfaces.OnEditTask;

import static com.dummies.tasks.provider.TaskProvider.COLUMN_TASKID;
import static com.dummies.tasks.provider.TaskProvider.COLUMN_TITLE;
import static com.dummies.tasks.provider.TaskProvider.CONTENT_URI;

public class TaskListFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    TaskListAdapter adapter;
    RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new TaskListAdapter();

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.task_list_fragment,
                container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.list_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_insert:
                ((OnEditTask) getActivity()).editTask(0);
                return true;
            case R.id.menu_settings:
                startActivity(new Intent(getActivity(),
                        TaskPreferencesActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:
                AdapterContextMenuInfo info =
                        (AdapterContextMenuInfo) item.getMenuInfo();
                getActivity().getContentResolver().delete(
                        ContentUris.withAppendedId(CONTENT_URI,
                                info.id), null, null);
                return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater mi = getActivity().getMenuInflater();
        mi.inflate(R.menu.list_menu_item_longpress, menu);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int ignored, final Bundle args) {
        return new CursorLoader(getActivity(), CONTENT_URI, null, null,
                null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to
        // onLoadFinished()
        // above is about to be closed. We need to make sure we are no
        // longer using it.
        adapter.swapCursor(null);
    }
}

class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.ViewHolder> {
    Cursor cursor;
    int titleColumnIndex;
    int idColumnIndex;


    public void swapCursor(Cursor c) {
        cursor = c;
        cursor.moveToFirst();
        titleColumnIndex = cursor.getColumnIndex(COLUMN_TITLE);
        idColumnIndex = cursor.getColumnIndex(COLUMN_TASKID);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        // create a new view
        TextView v = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_row, parent, false);

        // wrap it in a ViewHolder
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder,
                                 final int i) {
        // set the text
        cursor.moveToPosition(i);
        viewHolder.title.setText(cursor.getString(titleColumnIndex));

        // Set the click action
        viewHolder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long id = getItemId(i);
                ((OnEditTask) viewHolder.title.getContext()).editTask(id);
            }
        });

        //registerForContextMenu(viewHolder.title); // TODO not working
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
        TextView title;

        public ViewHolder(TextView itemView) {
            super(itemView);
            title = itemView;
        }

    }
}