package com.dummies.android.taskreminder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class ReminderListActivity extends FragmentActivity implements
        OnEditReminder {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminder_list);
    }

    @Override
    public void editReminder(long id) {
        startActivity(new Intent(this, ReminderEditActivity.class).putExtra(
                ReminderProvider.COLUMN_ROWID, id));
    }
}
