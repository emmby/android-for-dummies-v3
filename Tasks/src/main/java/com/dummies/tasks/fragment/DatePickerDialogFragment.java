package com.dummies.tasks.fragment;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import java.util.Calendar;

/**
 * A lightweight wrapper for a DatePickerDialog that wraps the dialog
 * in a fragment.
 */
public class DatePickerDialogFragment extends DialogFragment {
    static final String YEAR = "year";
    static final String MONTH = "month";
    static final String DAY = "day";

    public static DatePickerDialogFragment newInstance(
            Calendar date) {
        DatePickerDialogFragment fragment =
                new DatePickerDialogFragment();

        Bundle args = new Bundle();
        args.putInt(YEAR, date.get(Calendar.YEAR));
        args.putInt(MONTH, date.get(Calendar.MONTH));
        args.putInt(DAY, date.get(Calendar.DAY_OF_MONTH));
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Find the TaskEditFragment that created this dialog by name.
        // We'll use that fragment as the edit callback,
        // so that when the user chooses a new date in our datepicker
        // dialog, the dialog will call back into the edit fragment to
        // set the new date.
        OnDateSetListener callback = (OnDateSetListener)
                getFragmentManager()
                        .findFragmentByTag
                                (TaskEditFragment
                                        .DEFAULT_FRAGMENT_TAG);

        // Construct a new DatePicker dialog that will be hosted by
        // this fragment. Set its Year, Month, and Day to the values
        // specified in the args bundle
        Bundle args = getArguments();
        return new DatePickerDialog(getActivity(), callback,
                args.getInt(YEAR),
                args.getInt(MONTH),
                args.getInt(DAY));
    }
}