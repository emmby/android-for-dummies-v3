package com.dummies.android.taskreminder;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class ReminderListAndEditorActivity extends FragmentActivity implements
        OnEditReminder, OnFinishEditor {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminder_list_and_editor);
    }

    /**
     * Set the edit fragment, replacing the existing fragment if there's one
     * already there.
     */
    @Override
    public void editReminder(long id) {
        ReminderEditFragment fragment = new ReminderEditFragment();
        Bundle arguments = new Bundle();
        arguments.putLong(ReminderProvider.COLUMN_ROWID, id);
        fragment.setArguments(arguments);

        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        transaction.replace(R.id.edit_container, fragment,
                ReminderEditFragment.DEFAULT_EDIT_FRAGMENT_TAG);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void finishEditor() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment previousFragment = fragmentManager
                .findFragmentByTag(ReminderEditFragment.DEFAULT_EDIT_FRAGMENT_TAG);
        transaction.remove(previousFragment);
        transaction.commit();
    }

}
