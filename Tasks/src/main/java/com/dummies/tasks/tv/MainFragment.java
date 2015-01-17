package com.dummies.tasks.tv;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.database.CursorMapper;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.CursorObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.ObjectAdapter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;

import com.dummies.tasks.R;
import com.dummies.tasks.activity.TaskEditActivity;
import com.dummies.tasks.provider.TaskProvider;

import java.util.Calendar;


public class MainFragment extends BrowseFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final Object[] CATEGORIES[] = {
        new Object[]{ "All",new int[]{
            Calendar.YEAR,
            Calendar.DAY_OF_YEAR,
            Calendar.HOUR_OF_DAY,
            Calendar.MINUTE,
            Calendar.SECOND
        }
        },
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
            Calendar.HOUR_OF_DAY,
            Calendar.MINUTE,
            Calendar.SECOND
        }
        },
    };


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final BackgroundManager backgroundManager
            = BackgroundManager.getInstance(getActivity());
        backgroundManager.attach(getActivity().getWindow());

        setTitle(getString(R.string.app_name));

        // set fastLane (or headers) background color
        setBrandColor(getResources().getColor(R.color.primary));

        CardPresenter cardPresenter = new CardPresenter();

        CursorMapper simpleMapper = new CursorToTaskMapper();

        setOnItemViewSelectedListener(
            new OnItemViewSelectedListener() {
                @Override
                public void onItemSelected(
                    Presenter.ViewHolder
                        itemViewHolder,
                    Object item,
                    RowPresenter.ViewHolder
                        rowViewHolder, Row row) {
                    if (itemViewHolder == null)
                        return;

                    ImageCardView cardView =
                        ((CardPresenter.ViewHolder) itemViewHolder)
                            .cardView;
                    Drawable d = cardView.getMainImage();
                    if(d!=null) {
                        Bitmap b = ((BitmapDrawable) d).getBitmap();
                        backgroundManager.setBitmap(b);
                    }
                }
            }
        );

        setOnItemViewClickedListener(
            new OnItemViewClickedListener() {
                @Override
                public void onItemClicked(Presenter.ViewHolder
                                              itemViewHolder,
                                          Object item,
                                          RowPresenter.ViewHolder
                                              rowViewHolder, Row row) {
                    long id = ((Task) item).id;
                    startActivity(
                        new Intent(
                            getActivity(),
                            TaskEditActivity.class)
                            .putExtra(TaskProvider.COLUMN_TASKID, id));
                }
            }
        );

        ArrayObjectAdapter adapter = new ArrayObjectAdapter(new ListRowPresenter());

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
        ObjectAdapter adapter = getAdapter();
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
        ObjectAdapter adapter = getAdapter();
        ListRow row = (ListRow) adapter.get(id);
        CursorObjectAdapter rowAdapter = (CursorObjectAdapter) row
            .getAdapter();
        rowAdapter.swapCursor(null);
    }

}

