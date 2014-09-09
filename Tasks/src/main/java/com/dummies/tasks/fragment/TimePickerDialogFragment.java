package com.dummies.tasks.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;

import java.util.Calendar;

/**
 * A lightweight wrapper for a TimePickerDialog that wraps the dialog
 * in a fragment.
 */
public class TimePickerDialogFragment extends DialogFragment {
    static final String HOUR = "hour";
    static final String MINS = "mins";

    public static TimePickerDialogFragment newInstance(
            Calendar time) {

        TimePickerDialogFragment fragment =
                new TimePickerDialogFragment();

        Bundle args = new Bundle();
        args.putInt(HOUR, time.get(Calendar.HOUR_OF_DAY));
        args.putInt(MINS, time.get(Calendar.MINUTE));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Find the TaskEditFragment that created this dialog. We'll
        // use that fragment as the edit callback, so that when the user
        // chooses a new date in our datepicker dialog,
        // the dialog will call back into the edit fragment to set the
        // new date.
        OnTimeSetListener listener = (OnTimeSetListener)
                getFragmentManager()
                        .findFragmentByTag(
                                TaskEditFragment
                                        .DEFAULT_FRAGMENT_TAG);

        // Construct a new TimePicker Dialog that will be hosted by
        // this fragment. Set its Hour and Minutes to the values
        // specified in the args bundle
        Bundle args = getArguments();
        return new TimePickerDialog(getActivity(), listener,
                args.getInt(HOUR),
                args.getInt(MINS), false);
    }
}