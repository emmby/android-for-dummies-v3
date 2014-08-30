package com.dummies.android.taskreminder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class ReminderListActivity extends Activity implements
        OnEditReminder {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminder_list_activity);
    }

    @Override
    public void editReminder(long id) {
        startActivity(new Intent(this, ReminderEditActivity.class).putExtra(
                ReminderProvider.COLUMN_ROWID, id));
    }
}
