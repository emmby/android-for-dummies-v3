package com.dummies.tasks.activity;

import android.app.Activity;
import android.os.Bundle;

import com.dummies.tasks.fragment.PreferencesFragment;

/**
 * An activity for displaying and editing preferences.
 * Uses a TaskPreferencesFragment to do all of the dirty work.
 */
public class PreferencesActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(
                android.R.id.content,
                new PreferencesFragment()).commit();
    }

}


