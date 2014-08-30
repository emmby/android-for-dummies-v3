package com.dummies.tasks.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.dummies.tasks.interfaces.OnEditFinished;
import com.dummies.tasks.R;

/**
 * Our Reminder List and Edit activity for Tablets
 */
public class TaskListAndEditorActivity extends Activity implements
        com.dummies.tasks.interfaces.OnEditTask, OnEditFinished {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_list_and_editor_activity);
    }

    /**
     * Called when the user asks to edit or insert a task.
     */
    @Override
    public void editTask(long id) {
        // Create the fragment and set the task id
        com.dummies.tasks.fragment.TaskEditFragment fragment = new com.dummies.tasks.fragment.TaskEditFragment();
        Bundle arguments = new Bundle();
        arguments.putLong(com.dummies.tasks.provider.TaskProvider.COLUMN_TASKID, id);
        fragment.setArguments(arguments);

        // Add the fragment to the activity. If there's one already
        // there (eg. the user clicks on another task), replace it
        FragmentTransaction transaction = getFragmentManager()
                .beginTransaction();
        transaction.replace(R.id.edit_container, fragment,
                com.dummies.tasks.fragment.TaskEditFragment.DEFAULT_EDIT_FRAGMENT_TAG);
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
    public void finishEditingTask() {
        // Find the edit fragment, and remove it from the activity.
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager
                .beginTransaction();
        Fragment previousFragment = fragmentManager
                .findFragmentByTag(com.dummies.tasks.fragment
                        .TaskEditFragment
                        .DEFAULT_EDIT_FRAGMENT_TAG);
        transaction.remove(previousFragment);
        transaction.commit();
    }

}
