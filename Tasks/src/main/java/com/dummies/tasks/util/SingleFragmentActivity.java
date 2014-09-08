package com.dummies.tasks.util;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

/**
 * A simple wrapper for showing a single fragment.
 * Does not support adding or removing fragments from the activity.
 */
 //There is something wrong in this package. This is not a utility package. 
 //this class should be inside the activity package.
abstract public class SingleFragmentActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Add the fragment if it has not already been added to the
        // FragmentManager. If you don't do this a new Fragment will be
        // added every time this method is called (such as on
        // orientation change)
        if (savedInstanceState == null)
            getFragmentManager().beginTransaction().add(
                    android.R.id.content,
                    newFragmentInstance(),
                    getFragmentTag() ).commit();
    }

    /**
     * Implement this method to create a new instance of the Fragment
     * you wish to wrap.
     */
    protected abstract Fragment newFragmentInstance();

    /**
     * Implement this method to tag your fragment when it is added to
     * the activity.
     *
     * @see android.support.v4.app.FragmentManager#beginTransaction()
     */
    protected String getFragmentTag() {
        return null;
    }
}
