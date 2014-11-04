package com.dummies.tasks.tablet.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.Toolbar;

import com.dummies.tasks.R;
import com.dummies.tasks.fragment.TaskEditFragment;
import com.dummies.tasks.interfaces.OnEditFinished;
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
        setContentView(R.layout.activity_task_list_and_editor);

        // Tell Android that R.id.toolbar is our ActionBar
        setActionBar((Toolbar) findViewById(R.id.toolbar));
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
        FragmentTransaction ft = getFragmentManager()
                .beginTransaction();
        ft.replace(R.id.edit_container, fragment,
                TaskEditFragment.DEFAULT_FRAGMENT_TAG);

        // Add this change to the backstack, so that when the user
        // clicks the back button we'll pop this editor off the stack.
        // If we don't do this, the whole activity will close when the
        // user clicks the back button, which will be disruptive and
        // unexpected.
        ft.addToBackStack(null);

        // Make it so!
        ft.commit();
    }

    /**
     * Called when the user finishes editing a task.
     */
    @Override
    public void finishEditingTask() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm
                .beginTransaction();

        // Find the edit fragment, and remove it from the activity.
        Fragment previousFragment = fm
                .findFragmentByTag(
                        TaskEditFragment.DEFAULT_FRAGMENT_TAG);
        transaction.remove(previousFragment);

        transaction.commit();
    }

}
