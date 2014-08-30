package com.dummies.android.taskreminder;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

abstract public class SingleFragmentActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If not already added to the Fragment manager add it. If you
        // don't do this a new Fragment will be added every time this
        // method is called (such as on orientation change)
        if (savedInstanceState == null)
            getFragmentManager().beginTransaction().add(
                    android.R.id.content, newFragmentInstance()).commit();
    }

    protected abstract Fragment newFragmentInstance();
}
