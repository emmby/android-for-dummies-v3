package com.dummies.android.taskreminder.activity;

import android.app.Fragment;

import com.dummies.android.taskreminder.util.SingleFragmentActivity;
import com.dummies.android.taskreminder.fragment.TaskPreferencesFragment;

/**
 * An activity for displaying and editing preferences.
 * Uses a TaskPreferencesFragment to do all of the dirty work.
 */
public class TaskPreferencesActivity extends SingleFragmentActivity {

    @Override
    protected Fragment newFragmentInstance() {
        return new TaskPreferencesFragment();
    }
}


