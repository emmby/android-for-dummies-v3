package com.dummies.android.taskreminder;

import android.app.Fragment;

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


