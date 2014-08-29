package com.dummies.android.taskreminder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class ReminderEditFragment extends Fragment implements
        OnDateSetListener, OnTimeSetListener, LoaderCallbacks<Cursor> {
    public static final String DEFAULT_EDIT_FRAGMENT_TAG = "editFragmentTag";

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

    private EditText mTitleText;
    private EditText mBodyText;
    private Button mDateButton;
    private Button mTimeButton;
    private Button mConfirmButton;
    private long mRowId;
    private Calendar mCalendar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If we're restoring state from a previous activity, restore the
        // previous date as well, otherwise use now
        if (savedInstanceState != null
                && savedInstanceState.containsKey(CALENDAR)) {
            mCalendar = (Calendar) savedInstanceState.getSerializable(CALENDAR);
        } else {
            mCalendar = Calendar.getInstance();
        }

        Bundle arguments = getArguments();
        if (arguments != null) {
            mRowId = arguments.getLong(ReminderProvider.COLUMN_ROWID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.reminder_edit, container, false);

        mTitleText = (EditText) v.findViewById(R.id.title);
        mBodyText = (EditText) v.findViewById(R.id.body);
        mDateButton = (Button) v.findViewById(R.id.reminder_date);
        mTimeButton = (Button) v.findViewById(R.id.reminder_time);
        mConfirmButton = (Button) v.findViewById(R.id.confirm);

        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });

        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();
                values.put(ReminderProvider.COLUMN_ROWID, mRowId);
                values.put(ReminderProvider.COLUMN_TITLE, mTitleText.getText()
                        .toString());
                values.put(ReminderProvider.COLUMN_BODY, mBodyText.getText()
                        .toString());
                values.put(ReminderProvider.COLUMN_DATE_TIME,
                        mCalendar.getTimeInMillis());

                if (mRowId == 0) {
                    Uri itemUri = getActivity().getContentResolver().insert(
                            ReminderProvider.CONTENT_URI, values);
                    mRowId = ContentUris.parseId(itemUri);
                } else {
                    int count = getActivity().getContentResolver().update(
                            ContentUris.withAppendedId(
                                    ReminderProvider.CONTENT_URI, mRowId),
                            values, null, null);
                    if (count != 1)
                        throw new IllegalStateException("Unable to update "
                                + mRowId);
                }

                Toast.makeText(getActivity(),
                        getString(R.string.task_saved_message),
                        Toast.LENGTH_SHORT).show();
                ((OnFinishEditor) getActivity()).finishEditor();
                new ReminderManager(getActivity()).setReminder(mRowId,
                        mCalendar);
            }

        });

        if (mRowId == 0) {
            // This is a new task - add defaults from preferences if set.
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(getActivity());
            String defaultTitleKey = getString(R.string.pref_task_title_key);
            String defaultTimeKey = getString(R.string.pref_default_time_from_now_key);

            String defaultTitle = prefs.getString(defaultTitleKey, null);
            String defaultTime = prefs.getString(defaultTimeKey, null);

            if (defaultTitle != null)
                mTitleText.setText(defaultTitle);

            if (defaultTime != null && defaultTime.length()>0 )
                mCalendar.add(Calendar.MINUTE, Integer.parseInt(defaultTime));

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
        outState.putSerializable(CALENDAR, mCalendar);
    }

    private void showDatePicker() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        DialogFragment newFragment = new DatePickerDialogFragment();
        Bundle args = new Bundle();
        args.putInt(YEAR, mCalendar.get(Calendar.YEAR));
        args.putInt(MONTH, mCalendar.get(Calendar.MONTH));
        args.putInt(DAY, mCalendar.get(Calendar.DAY_OF_MONTH));
        newFragment.setArguments(args);
        newFragment.show(ft, "datePicker");
    }

    private void showTimePicker() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        DialogFragment newFragment = new TimePickerDialogFragment();
        Bundle args = new Bundle();
        args.putInt(HOUR, mCalendar.get(Calendar.HOUR_OF_DAY));
        args.putInt(MINS, mCalendar.get(Calendar.MINUTE));
        newFragment.setArguments(args);
        newFragment.show(ft, "timePicker");
    }

    private void updateButtons() {
        // Set the time button text
        SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT);
        String timeForButton = timeFormat.format(mCalendar.getTime());
        mTimeButton.setText(timeForButton);

        // Set the date button text
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        String dateForButton = dateFormat.format(mCalendar.getTime());
        mDateButton.setText(dateForButton);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear,
            int dayOfMonth) {
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, monthOfYear);
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        updateButtons();
    }

    @Override
    public void onTimeSet(TimePicker view, int hour, int minute) {
        mCalendar.set(Calendar.HOUR_OF_DAY, hour);
        mCalendar.set(Calendar.MINUTE, minute);
        updateButtons();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), ContentUris.withAppendedId(
                ReminderProvider.CONTENT_URI, mRowId), null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor reminder) {
        // Close this fragment down if the item we're editing was deleted
        if (reminder.getCount() == 0) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    ((OnFinishEditor) getActivity()).finishEditor();
                }
            });
            return;
        }

        mTitleText.setText(reminder.getString(reminder
                .getColumnIndexOrThrow(ReminderProvider.COLUMN_TITLE)));
        mBodyText.setText(reminder.getString(reminder
                .getColumnIndexOrThrow(ReminderProvider.COLUMN_BODY)));

        // Get the date from the database
        Long dateInMillis = reminder.getLong(reminder
                .getColumnIndexOrThrow(ReminderProvider.COLUMN_DATE_TIME));
        Date date = new Date(dateInMillis);
        mCalendar.setTime(date);

        updateButtons();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        // nothing to reset for this fragment
    }
}
