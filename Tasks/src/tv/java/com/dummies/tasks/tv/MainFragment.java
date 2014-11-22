package com.dummies.tasks.tv;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.database.CursorMapper;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.CursorObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemClickedListener;
import android.support.v17.leanback.widget.OnItemSelectedListener;
import android.support.v17.leanback.widget.Row;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.dummies.tasks.R;
import com.dummies.tasks.provider.TaskProvider;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.net.URI;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;


public class MainFragment extends BrowseFragment implements Target, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "MainFragment";

    private BackgroundManager mBackgroundManager;
    private Drawable mDefaultBackground;
    private DisplayMetrics mMetrics;
    private Timer mBackgroundTimer;
    private final Handler mHandler = new Handler();
    private URI mBackgroundURI;
    private ArrayObjectAdapter adapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onActivityCreated(savedInstanceState);

        prepareBackgroundManager();

        setupUIElements();

        loadRows();

        setupEventListeners();
    }

    private void loadRows() {
        adapter = new ArrayObjectAdapter(new
                ListRowPresenter());
        CardPresenter mCardPresenter = new CardPresenter();


        for( int i=0; i< MovieList.MOVIE_CATEGORY.length; ++i ) {
            HeaderItem header = new HeaderItem(i,
                (String)MovieList.MOVIE_CATEGORY[i][0], null);
            CursorObjectAdapter cursorObjectAdapter = new CursorObjectAdapter
                (mCardPresenter);
            cursorObjectAdapter.setMapper(
                new CursorMapper() {
                    @Override
                    protected void bindColumns(Cursor cursor) {

                    }

                    @Override
                    protected Movie bind(Cursor cursor) {
                        Movie m = new Movie();
                        m.setId(cursor.getInt(0));
                        m.setTitle(cursor.getString(1));
                        m.setDescription(cursor.getString(2));
                        return m;
                    }
                });
            adapter.add(new ListRow(header, cursorObjectAdapter));
        }

        setAdapter(adapter);

        LoaderManager loaderManager = getLoaderManager();
        for( int i=0; i<MovieList.MOVIE_CATEGORY.length; ++i)
            loaderManager.initLoader(i, null, this);



    }

    private void prepareBackgroundManager() {

        mBackgroundManager = BackgroundManager.getInstance(getActivity());
        mBackgroundManager.attach(getActivity().getWindow());

        mDefaultBackground = getResources().getDrawable(R.drawable.default_background);

        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics
            (mMetrics);
    }

    private void setupUIElements() {
        setTitle(getString(R.string.browse_title)); // Badge, when set, takes precedent
                                                    // over title
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);

        // set fastLane (or headers) background color
        setBrandColor(getResources().getColor(R.color.fastlane_background));
        // set search icon color
        setSearchAffordanceColor(
            getResources().getColor(
                R.color
                    .search_opaque));
    }

    private void setupEventListeners() {
        setOnItemSelectedListener(getDefaultItemSelectedListener());
        setOnItemClickedListener(getDefaultItemClickedListener());
        setOnSearchClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Implement your own " +
                        "in-app search", Toast.LENGTH_LONG)
                        .show();
            }
        });
    }

    protected OnItemSelectedListener getDefaultItemSelectedListener() {
        return new OnItemSelectedListener() {
            @Override
            public void onItemSelected(Object item, Row row) {
                if (item instanceof Movie) {
                    mBackgroundURI = ((Movie) item).getCardImageURI();
                    startBackgroundTimer();
                }
            }
        };
    }

    protected OnItemClickedListener getDefaultItemClickedListener() {
        return new OnItemClickedListener() {
            @Override
            public void onItemClicked(Object item, Row row) {
                if (item instanceof String) {
                    Toast.makeText(getActivity(), (String) item, Toast.LENGTH_SHORT)
                            .show();
                }
            }
        };
    }

    protected void updateBackground(URI uri) {
        Picasso.with(getActivity())
                .load(uri.toString())
                .resize(mMetrics.widthPixels, mMetrics.heightPixels)
                .centerCrop()
                .error(mDefaultBackground)
                .into(this);
    }

    private void startBackgroundTimer() {
        if (null != mBackgroundTimer) {
            mBackgroundTimer.cancel();
        }
        mBackgroundTimer = new Timer();
        mBackgroundTimer.schedule(new UpdateBackgroundTask(), 300);
    }

    private class UpdateBackgroundTask extends TimerTask {

        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mBackgroundURI != null) {
                        updateBackground(mBackgroundURI);
                    }
                }
            });

        }
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
        this.mBackgroundManager.setBitmap(bitmap);
    }

    @Override
    public void onBitmapFailed(Drawable drawable) {
        this.mBackgroundManager.setDrawable(drawable);
    }

    @Override
    public void onPrepareLoad(Drawable drawable) {
        // Do nothing, default_background manager has its own transitions
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle args) {
        Calendar calendar = Calendar.getInstance();
        int calendarFieldToZero = (Integer) MovieList.MOVIE_CATEGORY[id][1];
        calendar.set(calendarFieldToZero,calendar.getActualMinimum
                (calendarFieldToZero));

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
