package com.dummies.android.taskreminder;

import android.app.Fragment;
import android.content.Intent;

public class ReminderListActivity extends SingleFragmentActivity
        implements OnEditReminder {

    @Override
    protected Fragment newFragmentInstance() {
        // Create a new ReminderListFragment when requested.
        // This fragment doesn't need any params when it's created
        return new ReminderListFragment();
    }

    @Override
    public void editReminder(long id) {
        // When we are asked to edit a reminder, start the
        // ReminderEditActivity
        startActivity(new Intent(this, ReminderEditActivity.class)
                .putExtra(ReminderProvider.COLUMN_TASKID, id));
    }
}
