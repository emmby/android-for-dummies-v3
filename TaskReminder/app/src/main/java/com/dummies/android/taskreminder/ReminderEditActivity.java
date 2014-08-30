package com.dummies.android.taskreminder;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class ReminderEditActivity extends Activity implements
        OnFinishEditor {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminder_edit_activity);

        Fragment fragment = getFragmentManager().findFragmentByTag(
                ReminderEditFragment.DEFAULT_EDIT_FRAGMENT_TAG);

        if (fragment == null) {
            fragment = new ReminderEditFragment();
            Bundle args = new Bundle();
            args.putLong(ReminderProvider.COLUMN_ROWID, getIntent()
                    .getLongExtra(ReminderProvider.COLUMN_ROWID, 0L));
            fragment.setArguments(args);

            FragmentTransaction transaction = getFragmentManager()
                    .beginTransaction();
            transaction.add(R.id.edit_container, fragment,
                    ReminderEditFragment.DEFAULT_EDIT_FRAGMENT_TAG);
            transaction.commit();
        }
    }

    @Override
    public void finishEditor() {
        finish();
    }
}
