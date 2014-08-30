package com.dummies.android.taskreminder;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

/**
 * Our Reminder List and Edit activity for Tablets
 */
public class ReminderListAndEditorActivity extends Activity implements
        OnEditReminder, OnEditFinished {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminder_list_and_editor_activity);
    }

    /**
     * Called when the user asks to edit or insert a task.
     */
    @Override
    public void editReminder(long id) {
        // Create the fragment and set the task id
        ReminderEditFragment fragment = new ReminderEditFragment();
        Bundle arguments = new Bundle();
        arguments.putLong(ReminderProvider.COLUMN_TASKID, id);
        fragment.setArguments(arguments);

        // Add the fragment to the activity. If there's one already
        // there (eg. the user clicks on another task), replace it
        FragmentTransaction transaction = getFragmentManager()
                .beginTransaction();
        transaction.replace(R.id.edit_container, fragment,
                ReminderEditFragment.DEFAULT_EDIT_FRAGMENT_TAG);
        // Add this change to the backstack, so that when the user
        // clicks the back button we'll pop this editor off the stack.
        // If we don't do this, the whole activity will close when the
        // user clicks the back button, which will be disruptive and
        // unexpected.
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * Called when the user finishes editing a task.
     */
    @Override
    public void finishEditingReminder() {
        // Find the edit fragment, and remove it from the activity.
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager
                .beginTransaction();
        Fragment previousFragment = fragmentManager
                .findFragmentByTag(ReminderEditFragment
                        .DEFAULT_EDIT_FRAGMENT_TAG);
        transaction.remove(previousFragment);
        transaction.commit();
    }

}
