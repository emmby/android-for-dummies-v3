package com.dummies.tasks.activity;

import android.app.Fragment;

import com.dummies.tasks.util.SingleFragmentActivity;
import com.dummies.tasks.fragment.TaskPreferencesFragment;

/**
 * An activity for displaying and editing preferences.
 * Uses a TaskPreferencesFragment to do all of the dirty work.
 */
public class TaskPreferencesActivity extends SingleFragmentActivity {

    @Override
    protected Fragment newFragmentInstance() {
        // Create the new fragment and return it.
        // This fragment doesn't need any params when it's created
        return new TaskPreferencesFragment();
    }
}


