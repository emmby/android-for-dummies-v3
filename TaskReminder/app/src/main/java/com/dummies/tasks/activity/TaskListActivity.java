package com.dummies.tasks.activity;

import android.app.Fragment;
import android.content.Intent;

import com.dummies.tasks.util.SingleFragmentActivity;

/**
 * Our Reminder List activity for Phones
 */
public class TaskListActivity extends SingleFragmentActivity
        implements com.dummies.tasks.interfaces.OnEditTask {

    @Override
    protected Fragment newFragmentInstance() {
        // Create a new TaskListFragment when requested.
        // This fragment doesn't need any params when it's created
        return new com.dummies.tasks.fragment.TaskListFragment();
    }

    /**
     * Called when the user asks to edit or insert a task.
     */
    @Override
    public void editTask(long id) {
        // When we are asked to edit a reminder, start the
        // TaskEditActivity
        startActivity(new Intent(this, TaskEditActivity.class)
                .putExtra(com.dummies.tasks.provider.TaskProvider.COLUMN_TASKID, id));
    }
}
