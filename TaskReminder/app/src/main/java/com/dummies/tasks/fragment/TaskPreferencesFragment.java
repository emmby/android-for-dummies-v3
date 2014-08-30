package com.dummies.tasks.fragment;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;
import android.text.method.DigitsKeyListener;

import com.dummies.tasks.R;

/**
 * A fragment that reads a list of preferences from a file and shows
 * them to the user for editing.
 */
public class TaskPreferencesFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Construct the preferences screen from the XML config
        addPreferencesFromResource(R.xml.task_preferences);

        // Use the number keyboard when editing the time preference
        EditTextPreference timeDefault = (EditTextPreference)
                findPreference(getString(R.string
                        .pref_default_time_from_now_key));
        timeDefault.getEditText().setKeyListener(DigitsKeyListener
                .getInstance());
    }
}
