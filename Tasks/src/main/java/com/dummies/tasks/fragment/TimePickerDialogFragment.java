package com.dummies.tasks.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;

/**
 * A lightweight wrapper for a TimePickerDialog that wraps the dialog
 * in a fragment.
 */
public class TimePickerDialogFragment extends DialogFragment {
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
                                        .DEFAULT_EDIT_FRAGMENT_TAG);

        // Construct a new TimePicker Dialog that will be hosted by
        // this fragment. Set its Hour and Minutes to the values
        // specified in the args bundle
        Bundle args = getArguments();
        return new TimePickerDialog(getActivity(), listener,
                args.getInt(TaskEditFragment.HOUR),
                args.getInt(TaskEditFragment.MINS), false);
    }
}