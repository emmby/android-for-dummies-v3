package com.dummies.tasks.activity;

import android.app.Fragment;

import com.dummies.tasks.interfaces.OnEditFinished;
import com.dummies.tasks.util.SingleFragmentActivity;

/**
 * Our Reminder Edit Activity for Phones
 */
public class TaskEditActivity extends SingleFragmentActivity
        implements OnEditFinished {

    @Override
    protected Fragment newFragmentInstance() {

        // Create a new fragment and pass along arguments (like
        // COLUMN_TASKID) from the activity to the fragment
        Fragment fragment = new com.dummies.tasks.fragment.TaskEditFragment();
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
