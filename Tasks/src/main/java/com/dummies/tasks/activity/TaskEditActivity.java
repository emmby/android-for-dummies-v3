package com.dummies.tasks.activity;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.widget.Toolbar;

import com.dummies.tasks.R;
import com.dummies.tasks.fragment.TaskEditFragment;
import com.dummies.tasks.interfaces.OnEditFinished;

/**
 * Our Reminder Edit Activity for Phones
 */
public class TaskEditActivity extends Activity implements OnEditFinished {

    /**
     * Called when the user finishes editing a task.
     */
    @Override
    public void finishEditingTask() {
        // When the user dismisses the editor, call finish to destroy
        // this activity.
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_edit);
        setActionBar((Toolbar) findViewById(R.id.toolbar));

        // Create a new fragment and pass along arguments (like
        // COLUMN_TASKID) from the activity to the fragment
        Fragment fragment = new TaskEditFragment();
        fragment.setArguments(getIntent().getExtras());

        // The tag that we'll use to add the fragment to the activity.
        // This will allow us to reference this fragment from other
        // fragments, such as the Date and Time picker dialog fragments,
        // for example.
        String fragmentTag = TaskEditFragment.DEFAULT_FRAGMENT_TAG;

        // Add the fragment if it has not already been added to the
        // FragmentManager. If you don't do this a new Fragment will be
        // added every time this method is called (such as on
        // orientation change).  The fragment will be attached as a
        // child of the "container" view
        if (savedInstanceState == null)
            getFragmentManager().beginTransaction().add(
                    R.id.container,
                    fragment,
                    fragmentTag).commit();
    }
}
