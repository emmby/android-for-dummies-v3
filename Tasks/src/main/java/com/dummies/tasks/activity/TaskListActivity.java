package com.dummies.tasks.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.dummies.tasks.fragment.TaskListFragment;
import com.dummies.tasks.interfaces.OnEditTask;
import com.dummies.tasks.provider.TaskProvider;

/**
 * Our Reminder List activity for Phones
 */
public class TaskListActivity extends Activity implements OnEditTask {

    /**
     * Called when the user asks to edit or insert a task.
     */
    @Override
    public void editTask(long id) {
        // When we are asked to edit a reminder, start the
        // TaskEditActivity with the id of the task to edit.
        startActivity(new Intent(this, TaskEditActivity.class)
                .putExtra(TaskProvider.COLUMN_TASKID, id));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // The tag that we'll use to add the fragment to the activity.
        // This will allow us to reference this fragment from other
        // fragments, such as the Date and Time picker dialog fragments,
        // for example.
        String fragmentTag = TaskListFragment.DEFAULT_FRAGMENT_TAG;

        // Add the fragment if it has not already been added to the
        // FragmentManager. If you don't do this a new Fragment will be
        // added every time this method is called (such as on
        // orientation change)
        if (savedInstanceState == null)
            getFragmentManager().beginTransaction().add(
                    android.R.id.content,
                    new TaskListFragment(),
                    fragmentTag).commit();
    }

}
