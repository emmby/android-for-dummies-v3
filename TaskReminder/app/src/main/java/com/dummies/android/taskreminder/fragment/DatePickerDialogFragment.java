package com.dummies.android.taskreminder.fragment;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import static com.dummies.android.taskreminder.fragment.ReminderEditFragment.DAY;
import static com.dummies.android.taskreminder.fragment.ReminderEditFragment
        .DEFAULT_EDIT_FRAGMENT_TAG;
import static com.dummies.android.taskreminder.fragment.ReminderEditFragment.MONTH;
import static com.dummies.android.taskreminder.fragment.ReminderEditFragment.YEAR;

/**
 * A DialogFragment used to display a date picker
 */
public class DatePickerDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Find the ReminderEditFragment that created this dialog. We'll
        // use that fragment as the edit callback, so that when the user
        // chooses a new date in our datepicker dialog,
        // the dialog will call back into the edit fragment to set the
        // new date.
        OnDateSetListener callback = (OnDateSetListener)
                getFragmentManager()
                        .findFragmentByTag(DEFAULT_EDIT_FRAGMENT_TAG);

        // Construct a new DatePicker Dialog that will be hosted by
        // this fragment. Set its Year, Month, and Day to the values
        // specified in the args bundle
        Bundle args = getArguments();
        return new DatePickerDialog(getActivity(), callback,
                args.getInt(YEAR),
                args.getInt(MONTH),
                args.getInt(DAY));
    }
}