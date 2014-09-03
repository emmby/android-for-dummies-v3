package com.dummies.tasks.fragment;

import android.app.ActionBar;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.graphics.Palette;
import android.support.v7.graphics.PaletteItem;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.dummies.tasks.R;
import com.dummies.tasks.interfaces.OnEditFinished;
import com.dummies.tasks.util.ReminderManager;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.dummies.tasks.provider.TaskProvider.COLUMN_BODY;
import static com.dummies.tasks.provider.TaskProvider.COLUMN_DATE_TIME;
import static com.dummies.tasks.provider.TaskProvider.COLUMN_TASKID;
import static com.dummies.tasks.provider.TaskProvider.COLUMN_TITLE;
import static com.dummies.tasks.provider.TaskProvider.CONTENT_URI;

public class TaskEditFragment extends Fragment implements
        OnDateSetListener, OnTimeSetListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String DEFAULT_EDIT_FRAGMENT_TAG =
            "editFragmentTag";

    //
    // Dialog Constants
    //
    static final String YEAR = "year";
    static final String MONTH = "month";
    static final String DAY = "day";
    static final String HOUR = "hour";
    static final String MINS = "mins";
    static final String CALENDAR = "calendar";

    //
    // Date Format
    //
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String TIME_FORMAT = "kk:mm";

    View rootView;
    EditText titleText;
    EditText bodyText;
    ImageView imageView;
    Button dateButton;
    Button timeButton;
    ActionBar actionBar;
    long taskId;
    Calendar calendar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If we're restoring state from a previous activity, restore the
        // previous date as well
        if (savedInstanceState != null) {
            calendar = (Calendar) savedInstanceState.getSerializable
                    (CALENDAR);
        }

        // If we didn't have a previous date, use "now"
        if( calendar==null ) {
            calendar = Calendar.getInstance();
        }

        // Set the task id from the intent arguments, if available.
        Bundle arguments = getArguments();
        if (arguments != null) {
            taskId = arguments.getLong(COLUMN_TASKID,0L);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout and set the container. The layout is the
        // view that we will return.
        View v = inflater.inflate(R.layout.task_edit_fragment,
                container, false);

        // From the layout, get a few views that we're going to work with
        actionBar = getActivity().getActionBar();
        rootView = v;
        titleText = (EditText) v.findViewById(R.id.title);
        bodyText = (EditText) v.findViewById(R.id.body);
        dateButton = (Button) v.findViewById(R.id.task_date);
        timeButton = (Button) v.findViewById(R.id.task_time);
        imageView = (ImageView) v.findViewById(R.id.image);

        // Tell the date and time buttons what to do when we click on
        // them.
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });

        if (taskId == 0) {
            // This is a new task - add defaults from preferences if set.
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(getActivity());
            String defaultTitleKey = getString(R.string
                    .pref_task_title_key);
            String defaultTimeKey = getString(R.string
                    .pref_default_time_from_now_key);

            String defaultTitle = prefs.getString(defaultTitleKey, null);
            String defaultTime = prefs.getString(defaultTimeKey, null);

            if (defaultTitle != null)
                titleText.setText(defaultTitle);

            if (defaultTime != null && defaultTime.length() > 0)
                calendar.add(Calendar.MINUTE,
                        Integer.parseInt(defaultTime));

            updateButtons();

        } else {

            // Fire off a background loader to retrieve the data from the
            // database
            getLoaderManager().initLoader(0, null, this);

        }

        return v;
    }

    private void save() {
        // Put all the values the user entered into a
        // ContentValues object
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASKID, taskId);
        values.put(COLUMN_TITLE, titleText.getText().toString());
        values.put(COLUMN_BODY, bodyText.getText().toString());
        values.put(COLUMN_DATE_TIME, calendar.getTimeInMillis());

        // taskId==0 when we create a new task,
        // otherwise it's the id of the task being edited.
        if (taskId == 0) {

            // Create the new task and set taskId to the id of
            // the new task.
            Uri itemUri = getActivity().getContentResolver()
                    .insert(CONTENT_URI, values);
            taskId = ContentUris.parseId(itemUri);

        } else {

            // Edit the task
            int count = getActivity().getContentResolver().update(
                    ContentUris.withAppendedId(CONTENT_URI,
                            taskId),
                    values, null, null);

            // If somehow we didn't edit exactly one task,
            // throw an error
            if (count != 1)
                throw new IllegalStateException(
                        "Unable to update " + taskId);
        }

        // Notify the user of the change using a Toast
        Toast.makeText(getActivity(),
                getString(R.string.task_saved_message),
                Toast.LENGTH_SHORT).show();

        // Create a reminder for this task
        new ReminderManager(getActivity()).setReminder(taskId,
                calendar);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the calendar instance in case the user changed it
        outState.putSerializable(CALENDAR, calendar);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add(0, 1, 0, R.string.confirm).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Save button
        if( item.getItemId() == 1) {
            save();

            // Tell our enclosing activity that we are done so that
            // it can cleanup whatever it needs to clean up.
            ((OnEditFinished) getActivity()).finishEditingTask();

            return true;
        }

        // If we can't handle this menu item, see if our parent can
        return super.onOptionsItemSelected(item);
    }

    private void showDatePicker() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        DialogFragment newFragment = new DatePickerDialogFragment();
        Bundle args = new Bundle();
        args.putInt(YEAR, calendar.get(Calendar.YEAR));
        args.putInt(MONTH, calendar.get(Calendar.MONTH));
        args.putInt(DAY, calendar.get(Calendar.DAY_OF_MONTH));
        newFragment.setArguments(args);
        newFragment.show(ft, "datePicker");
    }

    private void showTimePicker() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        DialogFragment newFragment = new TimePickerDialogFragment();
        Bundle args = new Bundle();
        args.putInt(HOUR, calendar.get(Calendar.HOUR_OF_DAY));
        args.putInt(MINS, calendar.get(Calendar.MINUTE));
        newFragment.setArguments(args);
        newFragment.show(ft, "timePicker");
    }

    private void updateButtons() {
        // Set the time button text
        SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT);
        String timeForButton = timeFormat.format(calendar.getTime());
        timeButton.setText(timeForButton);

        // Set the date button text
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        String dateForButton = dateFormat.format(calendar.getTime());
        dateButton.setText(dateForButton);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear,
                          int dayOfMonth) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        updateButtons();
    }

    @Override
    public void onTimeSet(TimePicker view, int hour, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        updateButtons();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), ContentUris.withAppendedId(
                CONTENT_URI, taskId), null, null,
                null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor task) {
        // Close this fragmentClass down if the item we're editing was
        // deleted
        if (task.getCount() == 0) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    ((OnEditFinished) getActivity()).finishEditingTask();
                }
            });
            return;
        }

        titleText.setText(task.getString(task
                .getColumnIndexOrThrow(COLUMN_TITLE)));
        bodyText.setText(task.getString(task
                .getColumnIndexOrThrow(COLUMN_BODY)));

        // set the thumbnail image
        Picasso.with(getActivity())
                .load(getImageUrlForTask(getActivity(),taskId))
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        // Set the colors of the activity based on the
                        // colors of the image, if available
                        Bitmap bitmap = ((BitmapDrawable)imageView
                                .getDrawable())
                                .getBitmap();
                        Palette palette = Palette.generate(bitmap,24);

                        PaletteItem bgColor =
                                palette.getLightMutedColor();
                        PaletteItem fgColor =
                                palette.getLightVibrantColor();

                        if( bgColor!=null && fgColor!=null ) {
                            rootView.setBackgroundColor(bgColor.getRgb());
                            actionBar.setBackgroundDrawable(
                                    new ColorDrawable(fgColor.getRgb())
                            );
                        }
                    }

                    @Override
                    public void onError() {
                        // do nothing
                    }
                });


        // Get the date from the database
        Long dateInMillis = task.getLong(task
                .getColumnIndexOrThrow(
                        COLUMN_DATE_TIME));
        Date date = new Date(dateInMillis);
        calendar.setTime(date);

        updateButtons();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        // nothing to reset for this fragment
    }

    // TODO move this somewhere else
    public static String getImageUrlForTask(
            Context context, long taskId) {

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x; // in pixels
        int height = width * 2 / 3; // like a 4 x 6 photo

        return "http://lorempixel.com/" + width + "/" + height +
                "/cats/?fakeId=" + taskId;
    }
}
