package com.dummies.tasks.fragment;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.dummies.tasks.R;
import com.dummies.tasks.R.string;
import com.dummies.tasks.activity.TaskPreferencesActivity;

public class TaskListFragment extends ListFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private SimpleCursorAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create an array to specify the fields we want to display in
        // the list
        // (only TITLE)
        String[] from = new String[]{com.dummies.tasks.provider
                .TaskProvider.COLUMN_TITLE};

        // and an array of the fields we want to bind those fields to
        // (in this
        // case just text1)
        int[] to = new int[]{R.id.text1};

        // Now create a simple cursor adapter and set it to display
        adapter = new SimpleCursorAdapter(getActivity(),
                R.layout.task_row, null, from, to, 0);
        setListAdapter(adapter);

        getLoaderManager().initLoader(0, null, this);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setEmptyText(getResources().getString(string.no_tasks));
        registerForContextMenu(getListView());
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
                ((com.dummies.tasks.interfaces.OnEditTask) getActivity()).editTask(0);
                return true;
            case R.id.menu_settings:
                startActivity(new Intent(getActivity(),
                        TaskPreferencesActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position,
                                long id) {
        super.onListItemClick(l, v, position, id);
        ((com.dummies.tasks.interfaces.OnEditTask) getActivity()).editTask(id);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:
                AdapterContextMenuInfo info = (AdapterContextMenuInfo)
                        item
                        .getMenuInfo();
                getActivity().getContentResolver().delete(
                        ContentUris.withAppendedId(com.dummies.tasks.provider.TaskProvider
                                        .CONTENT_URI,
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
        return new CursorLoader(getActivity(),
                com.dummies.tasks.provider.TaskProvider.CONTENT_URI,
                null, null, null, null);
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
