package com.dummies.android.taskreminder;

import android.content.Intent;

public class ReminderListActivity extends SingleFragmentActivity implements
        OnEditReminder {

    public ReminderListActivity() {
        super(ReminderListFragment.class);
    }

    @Override
    public void editReminder(long id) {
        startActivity(new Intent(this, ReminderEditActivity.class).putExtra(
                ReminderProvider.COLUMN_ROWID, id));
    }
}
