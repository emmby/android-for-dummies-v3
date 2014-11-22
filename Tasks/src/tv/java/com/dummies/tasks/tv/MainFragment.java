package com.dummies.tasks.tv;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.database.CursorMapper;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.CursorObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;

import com.dummies.tasks.R;
import com.dummies.tasks.provider.TaskProvider;

import java.util.Calendar;


public class MainFragment extends BrowseFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final Object[] CATEGORIES[] = {
        new Object[]{ "Today", new int[]{
            Calendar.HOUR_OF_DAY,
            Calendar.MINUTE,
            Calendar.SECOND
        }
        },
        new Object[]{"This Week", new int[]{
            Calendar.DAY_OF_WEEK,
            Calendar.HOUR_OF_DAY,
            Calendar.MINUTE,
            Calendar.SECOND
        }
        },
        new Object[]{"This Month", new int[]{
            Calendar.DAY_OF_MONTH,
            Calendar.HOUR_OF_DAY,
            Calendar.MINUTE,
            Calendar.SECOND
        }
        },
        new Object[]{ "This Year",new int[]{
            Calendar.DAY_OF_YEAR,
            Calendar.DAY_OF_MONTH,
            Calendar.HOUR_OF_DAY,
            Calendar.MINUTE,
            Calendar.SECOND
        }
        },
        new Object[]{ "All",new int[]{
            Calendar.YEAR,
            Calendar.DAY_OF_YEAR,
            Calendar.HOUR_OF_DAY,
            Calendar.MINUTE,
            Calendar.SECOND
        }
        }
    };


    ArrayObjectAdapter adapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setTitle(getString(R.string.browse_title));

        // over title
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);

        // set fastLane (or headers) background color
        setBrandColor(
            getResources().getColor(R.color.fastlane_background));

        // set search icon color
        setSearchAffordanceColor(
            getResources().getColor(R.color.search_opaque));

        adapter = new ArrayObjectAdapter(new ListRowPresenter());
        CardPresenter cardPresenter = new CardPresenter();

        CursorMapper simpleMapper = new CursorMapper() {
            @Override
            protected void bindColumns(Cursor cursor) {
            }

            @Override
            protected Cursor bind(Cursor cursor) {
                return cursor;
            }
        };

        for( int i=0; i< CATEGORIES.length; ++i ) {
            HeaderItem header = new HeaderItem(i,
                (String)CATEGORIES[i][0], null);
            CursorObjectAdapter cursorObjectAdapter = new CursorObjectAdapter
                (cardPresenter);
            cursorObjectAdapter.setMapper(simpleMapper);

            adapter.add(new ListRow(header, cursorObjectAdapter));
        }

        setAdapter(adapter);

        LoaderManager loaderManager = getLoaderManager();
        for( int i=0; i<CATEGORIES.length; ++i)
            loaderManager.initLoader(i, null, this);


    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle args) {
        Calendar calendar = Calendar.getInstance();
        int[] calendarFieldsToZero = (int[])CATEGORIES[id][1];

        for( int fieldToZero : calendarFieldsToZero )
            calendar.set(
                fieldToZero,
                calendar.getActualMinimum(fieldToZero));

        return new CursorLoader(getActivity(),
            TaskProvider.CONTENT_URI,
            null,
            TaskProvider.COLUMN_DATE_TIME + "> ?",
            new String[]{Long.toString(calendar.getTimeInMillis())},
            null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        int id = loader.getId();
        ListRow row = (ListRow) adapter.get(id);
        CursorObjectAdapter rowAdapter = (CursorObjectAdapter) row
            .getAdapter();
        rowAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to
        // onLoadFinished()
        // above is about to be closed. We need to make sure we are no
        // longer using it.
        int id = loader.getId();
        ListRow row = (ListRow) adapter.get(id);
        CursorObjectAdapter rowAdapter = (CursorObjectAdapter) row
            .getAdapter();
        rowAdapter.swapCursor(null);
    }
}
