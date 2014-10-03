package com.dummies.tasks.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.dummies.tasks.R;
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
        setContentView(R.layout.activity_task_list);
    }

}
