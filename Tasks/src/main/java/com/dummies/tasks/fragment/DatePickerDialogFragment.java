package com.dummies.tasks.fragment;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

/**
 * A lightweight wrapper for a DatePickerDialog that wraps the dialog
 * in a fragment.
 */
public class DatePickerDialogFragment extends DialogFragment {
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
                                        .DEFAULT_EDIT_FRAGMENT_TAG);

        // Construct a new DatePicker dialog that will be hosted by
        // this fragment. Set its Year, Month, and Day to the values
        // specified in the args bundle
        Bundle args = getArguments();
        return new DatePickerDialog(getActivity(), callback,
                args.getInt(TaskEditFragment.YEAR),
                args.getInt(TaskEditFragment.MONTH),
                args.getInt(TaskEditFragment.DAY));
    }
}