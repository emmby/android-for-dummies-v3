package com.dummies.android.taskreminder.activity;

import android.app.Fragment;
import android.content.Intent;

import com.dummies.android.taskreminder.interfaces.OnEditReminder;
import com.dummies.android.taskreminder.fragment.ReminderListFragment;
import com.dummies.android.taskreminder.provider.ReminderProvider;
import com.dummies.android.taskreminder.util.SingleFragmentActivity;

/**
 * Our Reminder List activity for Phones
 */
public class ReminderListActivity extends SingleFragmentActivity
        implements OnEditReminder {

    @Override
    protected Fragment newFragmentInstance() {
        // Create a new ReminderListFragment when requested.
        // This fragment doesn't need any params when it's created
        return new ReminderListFragment();
    }

    /**
     * Called when the user asks to edit or insert a task.
     */
    @Override
    public void editReminder(long id) {
        // When we are asked to edit a reminder, start the
        // ReminderEditActivity
        startActivity(new Intent(this, ReminderEditActivity.class)
                .putExtra(ReminderProvider.COLUMN_TASKID, id));
    }
}
