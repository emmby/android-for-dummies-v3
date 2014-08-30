package com.dummies.android.taskreminder;

import android.app.DatePickerDialog.OnDateSetListener;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ReminderEditFragment extends Fragment implements
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

    private EditText titleText;
    private EditText bodyText;
    private Button dateButton;
    private Button timeButton;
    private long taskId;
    private Calendar calendar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If we're restoring state from a previous activity, restore the
        // previous date as well, otherwise use now
        if (savedInstanceState != null
                && savedInstanceState.containsKey(CALENDAR)) {
            calendar = (Calendar) savedInstanceState.getSerializable
                    (CALENDAR);
        } else {
            calendar = Calendar.getInstance();
        }

        Bundle arguments = getArguments();
        if (arguments != null) {
            taskId = arguments.getLong(ReminderProvider.COLUMN_TASKID,
                    0L);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.reminder_edit_fragment,
                container, false);

        titleText = (EditText) v.findViewById(R.id.title);
        bodyText = (EditText) v.findViewById(R.id.body);
        dateButton = (Button) v.findViewById(R.id.reminder_date);
        timeButton = (Button) v.findViewById(R.id.reminder_time);

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

        Button confirmButton = (Button) v.findViewById(R.id.confirm);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();
                values.put(ReminderProvider.COLUMN_TASKID, taskId);
                values.put(ReminderProvider.COLUMN_TITLE,
                        titleText.getText()
                        .toString());
                values.put(ReminderProvider.COLUMN_BODY,
                        bodyText.getText()
                        .toString());
                values.put(ReminderProvider.COLUMN_DATE_TIME,
                        calendar.getTimeInMillis());

                if (taskId == 0) {
                    Uri itemUri = getActivity().getContentResolver()
                            .insert(
                                    ReminderProvider.CONTENT_URI, values);
                    taskId = ContentUris.parseId(itemUri);
                } else {
                    int count = getActivity().getContentResolver().update(
                            ContentUris.withAppendedId(
                                    ReminderProvider.CONTENT_URI, taskId),
                            values, null, null);
                    if (count != 1)
                        throw new IllegalStateException("Unable to " +
                                "update "
                                + taskId);
                }

                Toast.makeText(getActivity(),
                        getString(R.string.task_saved_message),
                        Toast.LENGTH_SHORT).show();
                ((OnFinishEditor) getActivity()).finishEditor();
                new ReminderManager(getActivity()).setReminder(taskId,
                        calendar);
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the calendar instance in case the user changed it
        outState.putSerializable(CALENDAR, calendar);
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
                ReminderProvider.CONTENT_URI, taskId), null, null,
                null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor reminder) {
        // Close this fragmentClass down if the item we're editing was
        // deleted
        if (reminder.getCount() == 0) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    ((OnFinishEditor) getActivity()).finishEditor();
                }
            });
            return;
        }

        titleText.setText(reminder.getString(reminder
                .getColumnIndexOrThrow(ReminderProvider.COLUMN_TITLE)));
        bodyText.setText(reminder.getString(reminder
                .getColumnIndexOrThrow(ReminderProvider.COLUMN_BODY)));

        // Get the date from the database
        Long dateInMillis = reminder.getLong(reminder
                .getColumnIndexOrThrow(ReminderProvider
                        .COLUMN_DATE_TIME));
        Date date = new Date(dateInMillis);
        calendar.setTime(date);

        updateButtons();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        // nothing to reset for this fragmentClass
    }
}
