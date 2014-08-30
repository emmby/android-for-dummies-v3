package com.dummies.android.taskreminder;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class ReminderListAndEditorActivity extends Activity implements
        OnEditReminder, OnFinishEditor {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminder_list_and_editor_activity);
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

        FragmentTransaction transaction = getFragmentManager()
                .beginTransaction();
        transaction.replace(R.id.edit_container, fragment,
                ReminderEditFragment.DEFAULT_EDIT_FRAGMENT_TAG);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void finishEditor() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment previousFragment = fragmentManager
                .findFragmentByTag(ReminderEditFragment.DEFAULT_EDIT_FRAGMENT_TAG);
        transaction.remove(previousFragment);
        transaction.commit();
    }

}
