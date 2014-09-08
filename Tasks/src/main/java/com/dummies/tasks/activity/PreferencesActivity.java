package com.dummies.tasks.activity;

import android.app.Activity;
import android.os.Bundle;

import com.dummies.tasks.fragment.TaskPreferencesFragment;

/**
 * An activity for displaying and editing preferences.
 * Uses a TaskPreferencesFragment to do all of the dirty work.
 */
public class PreferencesActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // The tag that we'll use to add the fragment to the activity.
        // This will allow us to reference this fragment from other
        // fragments, such as the Date and Time picker dialog fragments,
        // for example.
        String fragmentTag = TaskPreferencesFragment.DEFAULT_FRAGMENT_TAG;

        // Add the fragment if it has not already been added to the
        // FragmentManager. If you don't do this a new Fragment will be
        // added every time this method is called (such as on
        // orientation change)
        if (savedInstanceState == null)
            getFragmentManager().beginTransaction().add(
                    android.R.id.content,
                    new TaskPreferencesFragment(),
                    fragmentTag ).commit();
    }

}


