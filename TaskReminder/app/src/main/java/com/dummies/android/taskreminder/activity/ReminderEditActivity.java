package com.dummies.android.taskreminder.activity;

import android.app.Fragment;

import com.dummies.android.taskreminder.interfaces.OnEditFinished;
import com.dummies.android.taskreminder.fragment.ReminderEditFragment;
import com.dummies.android.taskreminder.util.SingleFragmentActivity;

/**
 * Our Reminder Edit Activity for Phones
 */
public class ReminderEditActivity extends SingleFragmentActivity
        implements OnEditFinished {

    @Override
    protected Fragment newFragmentInstance() {

        // Create a new fragment and pass along arguments (like
        // COLUMN_TASKID) from the activity to the fragment
        Fragment fragment = new ReminderEditFragment();
        fragment.setArguments(getIntent().getExtras());
        return fragment;
    }

    /**
     * Called when the user finishes editing a task.
     */
    @Override
    public void finishEditingReminder() {
        // When the user dismisses the editor, call finish to destroy
        // this activity.
        finish();
    }
}
