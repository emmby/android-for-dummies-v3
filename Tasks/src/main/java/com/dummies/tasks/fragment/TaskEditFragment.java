package com.dummies.tasks.fragment;

import android.app.ActionBar;
import android.app.Activity;
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
import com.dummies.tasks.provider.TaskProvider;
import com.dummies.tasks.util.ReminderManager;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class TaskEditFragment extends Fragment implements
        OnDateSetListener, OnTimeSetListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    // The "name" that we'll use to identify this fragment
    public static final String DEFAULT_EDIT_FRAGMENT_TAG =
            "editFragment";

    // Various date and time constants
    static final String YEAR = "year";
    static final String MONTH = "month";
    static final String DAY = "day";
    static final String HOUR = "hour";
    static final String MINS = "mins";

    // Constants for saving instance state
    static final String TASK_ID = "taskId";
    static final String TASK_DATE_AND_TIME = "taskDateAndTime";

    // Views
    View rootView;
    EditText titleText;
    EditText notesText;
    ImageView imageView;
    Button dateButton;
    Button timeButton;
    ActionBar actionBar;

    // Some information about this task that we'll store here until we
    // save it to the database
    long taskId;
    Calendar taskDateAndTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If we're restoring state from a previous activity, restore the
        // previous date as well
        if (savedInstanceState != null) {
            taskId = savedInstanceState.getLong(TASK_ID);
            taskDateAndTime =
                    (Calendar) savedInstanceState.getSerializable
                            (TASK_DATE_AND_TIME);
        }

        // If we didn't have a previous date, use "now"
        if (taskDateAndTime == null) {
            taskDateAndTime = Calendar.getInstance();
        }

        // Set the task id from the intent arguments, if available.
        Bundle arguments = getArguments();
        if (arguments != null) {
            taskId = arguments.getLong(TaskProvider.COLUMN_TASKID, 0L);
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
        rootView = v.getRootView();
        titleText = (EditText) v.findViewById(R.id.title);
        notesText = (EditText) v.findViewById(R.id.notes);
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
                taskDateAndTime.add(Calendar.MINUTE,
                        Integer.parseInt(defaultTime));

            updateDateAndTimeButtons();

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
        String title = titleText.getText().toString();
        ContentValues values = new ContentValues();
        values.put(TaskProvider.COLUMN_TASKID, taskId);
        values.put(TaskProvider.COLUMN_TITLE, title);
        values.put(TaskProvider.COLUMN_NOTES, notesText.getText()
                .toString());
        values.put(TaskProvider.COLUMN_DATE_TIME,
                taskDateAndTime.getTimeInMillis());

        // taskId==0 when we create a new task,
        // otherwise it's the id of the task being edited.
        if (taskId == 0) {

            // Create the new task and set taskId to the id of
            // the new task.
            Uri itemUri = getActivity().getContentResolver()
                    .insert(TaskProvider.CONTENT_URI, values);
            taskId = ContentUris.parseId(itemUri);

        } else {

            // Edit the task
            int count = getActivity().getContentResolver().update(
                    ContentUris.withAppendedId(TaskProvider.CONTENT_URI,
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
        new ReminderManager(getActivity()).setReminder(
                taskId, title, taskDateAndTime);
    }

    /**
     * Android Activities can be killed at any time to save memory.  If
     * the user returns to the activity, it may be re-created again.
     * When it's killed, the Android OS will automatically
     * save the current state of any views (such as EditTexts) that the
     * user may have changed into the outState Bundle.  When it's
     * re-created, those values will be automatically set for you in
     * onCreate from the savedInstanceState bundle.
     *
     * However, while Android can do that for views for you
     * automatically, Android cannot do that for non-views.  So any
     * time you have any state that you or the user may modify in your
     * fragments or activities, it's UP TO YOU to make sure you save
     * them properly!  To do this, just save the value to the outState
     * bundle, and then read it back in again from the
     * savedInstanceState bundle in onCreate.
     *
     * DO NOT FORGET TO DO THIS!
     *
     * If you forget, your app will appear to work fine.  You won't
     * immediately notice anything wrong.  But anytime you rotate your
     * phone, or leave an app running for awhile in the background and
     * then later return to it, you may experience random crashes and
     * unexpected behavior.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // These two fields may have changed while our activity was
        // running, so make sure we save them to our outState bundle so
        // we can restore them later in onCreate.
        outState.putLong(TASK_ID, taskId);
        outState.putSerializable(TASK_DATE_AND_TIME, taskDateAndTime);
    }


    /**
     * Called when Android wants to create the options menu for our
     * activity.  In this case, our options menu is our action bar.
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        // Create a menu item named "Save" and give it an id of 1.
        // If you have multiple menu items, it's a good practice to
        // create static final ints to name them.
        menu.add(0, 1, 0, R.string.confirm)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            // The Save button was pressed
            case 1:
                save();

                // Tell our enclosing activity that we are done so that
                // it can cleanup whatever it needs to clean up.
                ((OnEditFinished) getActivity()).finishEditingTask();

                return true;
        }

        // If we can't handle this menu item, see if our parent can
        return super.onOptionsItemSelected(item);
    }

    /**
     * A helper method to show our Date picker
     */
    private void showDatePicker() {
        // Create a fragment transaction
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        // Create the DatePickerDialogFragment and initialize it with
        // the appropriate values.
        DialogFragment newFragment = new DatePickerDialogFragment();
        Bundle args = new Bundle();
        args.putInt(YEAR, taskDateAndTime.get(Calendar.YEAR));
        args.putInt(MONTH, taskDateAndTime.get(Calendar.MONTH));
        args.putInt(DAY, taskDateAndTime.get(Calendar.DAY_OF_MONTH));
        newFragment.setArguments(args);

        // Show the dialog, and name it "datePicker".  By naming it,
        // Android can automatically manage its state for us if it
        // needs to be killed and recreated.
        newFragment.show(ft, "datePicker");
    }

    private void showTimePicker() {
        // Create a fragment transaction
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        // Create the TimePickerDialogFragment and initialize it with
        // the appropriate values.
        DialogFragment newFragment = new TimePickerDialogFragment();
        Bundle args = new Bundle();
        args.putInt(HOUR, taskDateAndTime.get(Calendar.HOUR_OF_DAY));
        args.putInt(MINS, taskDateAndTime.get(Calendar.MINUTE));
        newFragment.setArguments(args);

        // Show the dialog, and name it "timePicker".  By naming it,
        // Android can automatically manage its state for us if it
        // needs to be killed and recreated.
        newFragment.show(ft, "timePicker");
    }

    /**
     * Call this method whenever the task's date/time has changed and
     * we need to update our date and time buttons.
     */
    private void updateDateAndTimeButtons() {
        // Set the time button text
        DateFormat timeFormat =
                DateFormat.getTimeInstance(DateFormat.SHORT);
        String timeForButton = timeFormat.format(
                taskDateAndTime.getTime());
        timeButton.setText(timeForButton);

        // Set the date button text
        DateFormat dateFormat = DateFormat.getDateInstance();
        String dateForButton = dateFormat.format(
                taskDateAndTime.getTime());
        dateButton.setText(dateForButton);
    }

    /**
     * This is the method that our Date Picker Dialog will call when
     * the user picks a date in the dialog.
     */
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear,
                          int dayOfMonth) {
        taskDateAndTime.set(Calendar.YEAR, year);
        taskDateAndTime.set(Calendar.MONTH, monthOfYear);
        taskDateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        updateDateAndTimeButtons();
    }

    /**
     * This is the method that our Time Picker Dialog will call when
     * the user picks a time in the dialog.
     */
    @Override
    public void onTimeSet(TimePicker view, int hour, int minute) {
        taskDateAndTime.set(Calendar.HOUR_OF_DAY, hour);
        taskDateAndTime.set(Calendar.MINUTE, minute);
        updateDateAndTimeButtons();
    }

    /**
     * This method is called when Android needs to create a loader to
     * load our task from the database.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Compute the URI for the task we want to load
        Uri taskUri = ContentUris.withAppendedId(
                TaskProvider.CONTENT_URI, taskId);

        // Create a cursor loader
        return new CursorLoader(getActivity(),
                taskUri, null, null, null, null);
    }

    /**
     * This methid is called when the loader has finished loading its
     * data
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor task) {
        // Sanity check.  If we weren't able to load anything,
        // then just close this activity.
        if (task.getCount() == 0) {
            // onLoadFinished is called from a background thread.  Many
            // operations that affect the UI aren't allowed from
            // background threads.  So make sure that we call
            // finishEditingTask from the UI thread instead of from a
            // background thread.
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((OnEditFinished) getActivity()).finishEditingTask();
                }
            });
            return;
        }

        // Set our title and notes from the DB
        titleText.setText(task.getString(task
                .getColumnIndexOrThrow(TaskProvider.COLUMN_TITLE)));
        notesText.setText(task.getString(task
                .getColumnIndexOrThrow(TaskProvider.COLUMN_NOTES)));

        // The our date from the database
        Long dateInMillis = task.getLong(task
                .getColumnIndexOrThrow(
                        TaskProvider.COLUMN_DATE_TIME));
        Date date = new Date(dateInMillis);
        taskDateAndTime.setTime(date);

        // Set the thumbnail image
        Picasso.with(getActivity())
                .load(getImageUrlForTask(getActivity(), taskId))
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        // Set the colors of the activity based on the
                        // colors of the image, if available
                        Bitmap bitmap = ((BitmapDrawable) imageView
                                .getDrawable())
                                .getBitmap();
                        Palette palette = Palette.generate(bitmap, 32);

                        PaletteItem bgColor =
                                palette.getLightMutedColor();
                        PaletteItem actionbarColor =
                                palette.getDarkVibrantColor();
                        PaletteItem statusColor =
                                palette.getDarkMutedColor();

                        if (bgColor != null && actionbarColor != null) {
                            rootView.setBackgroundColor(bgColor.getRgb());
                            actionBar.setBackgroundDrawable(
                                    new ColorDrawable(actionbarColor
                                            .getRgb())
                            );
                            ((Activity) rootView.getContext())
                                    .getWindow()
                                    .setStatusBarColor(
                                            (statusColor != null ?
                                                    statusColor :
                                                    actionbarColor)
                                                    .getRgb());
                        }
                    }

                    @Override
                    public void onError() {
                        // do nothing, we'll use the default colors
                    }
                });

        // We updated our task info, so update the date and time buttons
        updateDateAndTimeButtons();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        // nothing to reset for this fragment.
        // TODO i don't need to close the cursor?
    }

    // TODO move this somewhere else
    public static String getImageUrlForTask(
            Context context, long taskId) {

        WindowManager wm = (WindowManager) context.getSystemService
                (Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x; // in pixels
        int height = width * 2 / 3; // like a 4 x 6 photo

        return "http://lorempixel.com/" + width + "/" + height +
                "/cats/?fakeId=" + taskId;
    }
}
