package com.dummies.android.taskreminder;

import android.app.Fragment;

public class ReminderEditActivity extends SingleFragmentActivity
        implements OnFinishEditor {

    @Override
    protected Fragment newFragmentInstance() {
        Fragment fragment = new ReminderEditFragment();

        // pass along arguments (like COLUMN_TASKID) from the activity
        // to the fragment
        fragment.setArguments(getIntent().getExtras());
        return fragment;
    }

    @Override
    public void finishEditor() {
        finish();
    }
}
