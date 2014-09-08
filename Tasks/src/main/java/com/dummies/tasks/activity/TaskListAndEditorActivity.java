package com.dummies.tasks.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.dummies.tasks.fragment.TaskEditFragment;
import com.dummies.tasks.interfaces.OnEditFinished;
import com.dummies.tasks.R;
import com.dummies.tasks.interfaces.OnEditTask;
import com.dummies.tasks.provider.TaskProvider;

/**
 * Our Reminder List and Edit activity for Tablets
 */
public class TaskListAndEditorActivity extends Activity
        implements OnEditTask, OnEditFinished {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up the layout from an XML file
        setContentView(R.layout.task_list_and_editor_activity);
    }

    /**
     * Called when the user asks to edit or insert a task.
     */
    @Override
    public void editTask(long id) {
        // Create the fragment and set the task id
        TaskEditFragment fragment = new TaskEditFragment();
        Bundle arguments = new Bundle();
        arguments.putLong(TaskProvider.COLUMN_TASKID, id);
        fragment.setArguments(arguments);

        // Add the fragment to the activity. If there's one already
        // there (eg. the user clicks on another task), replace it
        FragmentTransaction transaction = getFragmentManager()
                .beginTransaction();
        transaction.replace(R.id.edit_container, fragment,
                TaskEditFragment.DEFAULT_EDIT_FRAGMENT_TAG);

        // Add this change to the backstack, so that when the user
        // clicks the back button we'll pop this editor off the stack.
        // If we don't do this, the whole activity will close when the
        // user clicks the back button, which will be disruptive and
        // unexpected.
        transaction.addToBackStack(null);

        // Make it so!
        transaction.commit();
    }

    /**
     * Called when the user finishes editing a task.
     */
    @Override
    public void finishEditingTask() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        // Find the edit fragment, and remove it from the activity.
        Fragment previousFragment = fm.findFragmentByTag(
            TaskEditFragment.DEFAULT_EDIT_FRAGMENT_TAG);
        ft.remove(previousFragment);
        ft.commit();
    }

}
