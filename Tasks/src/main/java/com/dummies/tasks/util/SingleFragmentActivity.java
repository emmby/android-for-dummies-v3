package com.dummies.tasks.util;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

/**
 * A simple wrapper for showing a single fragment.
 * Does not support adding or removing fragments from the activity.
 */
abstract public class SingleFragmentActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Add the fragment if it has not already been added to the
        // FragmentManager. If you don't do this a new Fragment will be
        // added every time this method is called (such as on
        // orientation change)
        
        //I don't see this as a good practice. The test here should be 
        //to ask the fragment manager if it knows the fragment identified by the tag.
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
        //the tag could be fixed in this class, no need to override.
        //the fragment manager can't get confused, they will be different instances.
        return null;
    }
}
